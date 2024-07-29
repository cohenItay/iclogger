package com.itayc.loggers_android.disklogger

import android.content.Context
import com.itayc.loggers_android.disklogger.worker.CleanLogsWorker
import com.itayc.loggers_android.disklogger.worker.InitCleanLogWorkerIfNeededUseCase
import com.itayc.loggers_android.disklogger.worker.WorkerFactoryHolder
import kotlinx.coroutines.CoroutineDispatcher
import java.util.Locale

// The focal point of where the client would build an instance of the [DiskLogger]
/**
 * A builder which helps to create an instance of [DiskLogger].
 * Use [DiskLoggerBuilder.buildWith] in order to do so
 */
@Suppress("unused")
class DiskLoggerBuilder {

    private val initCleanUseCase by lazy { InitCleanLogWorkerIfNeededUseCase() }

    /**
     * the required file provider which will supply the file to write the logs to.
     * when null, the [LogsFileProviderDaily] will be used, constructed with [Locale.getDefault].
     */
    var fileProvider: LogsFileProvider? = null

    /**
     * for how long would you prefer that the logs will be kept on disk.
     * when null, no logic will be invoked in order to delete the logs.
     */
    var keepLogsPeriod: Int? = null

    /**
     * Sets the locale which affect the time of log file separation
     */
    var locale: Locale = Locale.getDefault()



    private fun build(appContext: Context, dispatcherIo: CoroutineDispatcher) = DiskLoggerImpl(
        appContext = appContext,
        logsFileProvider = fileProvider ?: LogsFileProviderDaily(locale),
        dispatcherIo = dispatcherIo
    ).also { diskLogger ->
        instantiateCleanWorkerIfNeeded(appContext, diskLogger)
    }

    private fun instantiateCleanWorkerIfNeeded(appContext: Context, diskLogger: DiskLoggerImpl) {
        val klp = keepLogsPeriod ?: return
        // The user wants us to delete the logs for him:
        if (WorkerFactoryHolder.workerFactory == null)
            WorkerFactoryHolder.workerFactory = CleanLogsWorker.Factory(diskLogger, locale) // keep a reference to the true factory which create the CleanLogsWorker with internal injected constructor
        initCleanUseCase(appContext, klp)
    }

    companion object {

        /**
         * Simplifies the usage of [DiskLoggerBuilder]
         */
        fun buildWith(appContext: Context, dispatcherIo: CoroutineDispatcher, optionsBuilder: DiskLoggerBuilder.() -> Unit): DiskLogger =
            DiskLoggerBuilder().apply(optionsBuilder).build(appContext, dispatcherIo)
    }
}