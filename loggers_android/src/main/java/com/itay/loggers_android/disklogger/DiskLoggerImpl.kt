package com.itay.loggers_android.disklogger

import android.content.Context
import android.util.Log
import com.itay.iclogger.LogLevel
import com.itay.iclogger.appendAttrsToLog
import com.itay.iclogger.appendLevelTagThrowable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

internal class DiskLoggerImpl(
    private val appContext: Context,
    private val logsFileProvider: LogsFileProvider,
    private val dispatcherIo: CoroutineDispatcher
) : DiskLogger {

    private val tag = DiskLogger::class.simpleName!!
    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private var bufferWriter: BufferedWriter? = null
    private val loggerScope = CoroutineScope(dispatcherIo + SupervisorJob())
    private val channel = Channel<Operation>()
    private var closeBufferJob: Job? = null
    private val mutex = Mutex()

    init {
        loggerScope.launch {
            for (op in channel) {
                when (op) {
                    is Operation.Write ->
                        processWriteLog(op.logContent)
                    is Operation.Delete ->
                        processCleanLogs(op.from)
                }
            }
        }
    }

    override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?) {
        val logContent = message
            .appendAttrsToLog(attributes)
            .appendLevelTagThrowable(LogLevel.INFO, tag, throwable)
        loggerScope.launch {
            // Working with the channel makes the communications between the coroutines sequential
            // and thus works like a queue
            channel.send(Operation.Write(logContent))
        }
    }

    override suspend fun flushToDisk() {
        withContext(dispatcherIo) {
            @Suppress("BlockingMethodInNonBlockingContext")
            bufferWriter?.flush()
        }
    }

    override suspend fun releaseResources() {
        flushToDisk()
    }

    override fun cleanLogs(from: Date) {
        val diff = TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - from.time)
        if (diff < 1L)
            error("Less then one day time difference")
        loggerScope.launch {
            channel.send(Operation.Delete(from))
        }
    }

    private suspend fun processCleanLogs(from: Date) = withContext(Dispatchers.IO) {
        if (Calendar.getInstance().time.time <= from.time)
            return@withContext
        val directory = logsFileProvider.provideLogsDirectory(appContext)
        if (!directory.exists())
            return@withContext
        val allInnerFiles = FileUtils.getAllFilesInDir(directory)
        val filesToDelete = allInnerFiles.filter { file ->
            val lastModified = file.lastModified()
            if (lastModified <= 0L) {
                false
            } else {
                Date(lastModified).before(from)
            }
        }
        filesToDelete.forEach { file ->
            if (!file.delete()) {
                Log.e(tag, "processCleanLogs: Was not able to delete file ${file.name}")
            }
        }
    }

    /**
     * Process the log, which is write it to the disk.
     */
    private suspend fun processWriteLog(logContent: String) = withContext(Dispatchers.IO) {
        val writer = validateWriterIsReady() ?: return@withContext
        val time = timeFormat.format(Calendar.getInstance().time)
        val log = "${Thread.currentThread().name} $time $logContent\n"
        try {
            writer.write(log)
        } catch (e: IOException) {
            Log.e(tag, "couldn't write the log: '$log'", e)
        }
        closeBufferJob?.cancelAndJoin()
        closeBufferJob = createCloseBufferJob()
    }

    @Suppress( "BlockingMethodInNonBlockingContext") // This lint is shown for withContext while it should not, bug.
    private suspend fun validateWriterIsReady() : BufferedWriter? = mutex.withLock {
        bufferWriter ?: withContext(dispatcherIo) {
            val file = logsFileProvider.provideFile(appContext)
                ?: return@withContext null
            try {
                bufferWriter = BufferedWriter(FileWriter(file, true))
            } catch (e: IOException) {
                Log.e(tag, "$file, is a directory rather then a file", e)
                bufferWriter?.close()
            }
            bufferWriter!!
        }
    }

    private fun createCloseBufferJob() = loggerScope.launch(Dispatchers.IO) {
        delay(2 * 60 * 1_000L) // 2 minutes
        if (isActive) {
            closeBuffer()
        }
    }

    private fun closeBuffer() {
        bufferWriter?.close()
        bufferWriter = null
    }

    private sealed class Operation {
        data class Write(val logContent: String) : Operation()
        data class Delete(val from: Date) : Operation()
    }
}