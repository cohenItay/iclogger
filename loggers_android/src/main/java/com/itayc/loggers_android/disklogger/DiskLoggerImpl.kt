package com.itayc.loggers_android.disklogger

import android.content.Context
import android.util.Log
import com.itayc.iclogger.LogLevel
import com.itayc.iclogger.appendAttrsToLog
import com.itayc.iclogger.appendLevelTagThrowable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

internal class DiskLoggerImpl(
    private val appContext: Context,
    private val logsFileProvider: LogsFileProvider,
    private val dispatcherIo: CoroutineDispatcher,
    locale: Locale,
    timeZone: TimeZone
) : DiskLogger, ImmediateLogging {

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", locale).also { it.timeZone = timeZone }
    private var bufferWriter: Writer? = null
    private val loggerScope = CoroutineScope(dispatcherIo + SupervisorJob())
    private val channel = Channel<Operation>(capacity = Channel.UNLIMITED)
    private var closeBufferJob: Job? = null
    private val writeMutex = Mutex()

    init {
        loggerScope.launch {
            for (op in channel) {
                when (op) {
                    is Operation.Write ->
                        processWriteLogDispatched(op.logContent)
                    is Operation.Delete ->
                        processCleanLogs(op.from)
                    is Operation.Flush ->
                        processFlushLogs(op.ack)
                }
            }
        }
    }

    override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?) {
        val time = timeFormat.format(Calendar.getInstance().time)
        val threadInfo = String.format("%.30s", Thread.currentThread().name)
        val logContent = message
            .appendAttrsToLog(attributes)
            .appendLevelTagThrowable(logLevel, tag, throwable)
            .let { "$threadInfo $time $it" }
        loggerScope.launch {
            // Working with the channel makes the communications between the coroutines sequential
            // and thus works like a queue
            channel.send(Operation.Write(logContent))
        }
    }

    override fun immediateWriteLog(logContent: String) {
        loggerScope.launch(start = CoroutineStart.UNDISPATCHED, context = NonCancellable) {
            processWriteLog(logContent = logContent, flush = true)
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun flushToDiskBlocking() {
        runBlocking {
            val deferred = CompletableDeferred(Unit)
            channel
                .trySend(Operation.Flush(deferred))
                .onSuccess {
                    deferred.await()
                }
        }
    }

    override fun releaseResources() {
        flushToDiskBlocking()
        channel.close()
        closeBuffer()
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
                Log.e(TAG, "processCleanLogs: Was not able to delete file ${file.name}")
            }
        }
    }

    /**
     * Process the log, which is write it to the disk.
     */
    private suspend fun processWriteLogDispatched(logContent: String) = withContext(Dispatchers.IO) {
        processWriteLog(logContent)
    }

    private suspend fun processWriteLog(logContent: String, flush: Boolean = false) = writeMutex.withLock {
        val writer = validateWriterIsReady() ?: return@withLock
        currentCoroutineContext().ensureActive()
        try {
            writer.write("$logContent\n")
            if (flush)
                writer.flush()
        } catch (e: IOException) {
            Log.e(TAG, "couldn't write the log: '$logContent'", e)
        }
        closeBufferJob?.cancelAndJoin()
        closeBufferJob = createCloseBufferJob()
    }


    private fun processFlushLogs(deferred: CompletableDeferred<Unit>) {
        bufferWriter?.flush()
        deferred.complete(Unit)
    }

    @Suppress( "BlockingMethodInNonBlockingContext") // This lint is shown for withContext while it should not, bug.
    private suspend fun validateWriterIsReady() : Writer? =
        bufferWriter ?: withContext(dispatcherIo) {
            ensureActive()
            val file = logsFileProvider.provideFile(appContext)
                ?: return@withContext null
            try {
                bufferWriter = FileWriter(file, true)
            } catch (e: IOException) {
                Log.e(TAG, "$file, is a directory rather then a file", e)
                bufferWriter?.close()
            }
            bufferWriter!!
        }

    private fun createCloseBufferJob() = loggerScope.launch(Dispatchers.IO) {
        delay(10 * 60 * 1_000L) // 10 minutes
        if (isActive) {
            closeBuffer()
        }
    }

    private fun closeBuffer() {
        runCatching {
            bufferWriter?.close()
            bufferWriter = null
        }
    }

    private sealed class Operation {
        data class Write(val logContent: String) : Operation()
        data class Delete(val from: Date) : Operation()
        data class Flush(val ack: CompletableDeferred<Unit>) : Operation()
    }

    companion object {
        private const val TAG = "DiskLoggerImpl"
    }
}