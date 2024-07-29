package com.itay.loggers_android.disklogger.worker

import android.content.Context
import androidx.annotation.Keep
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

/**
 * A worker which can be instantiated with the client app default [WorkerFactory.getDefaultWorkerFactory]
 * (without special constructor injection other then the defaults).
 *
 * This Worker will delegate the work to this library internal worker which can contain constructor injections.
 */
@Keep // This class should be aware to reflection
class CleanLogsDummyWorker constructor(
    appContext : Context,
    param : WorkerParameters,
) : CoroutineWorker(appContext , param) {

    private val delegateWorker by lazy {
        WorkerFactoryHolder.workerFactory?.create(appContext, param) ?: error("Worker factory must exists")
    }

    override suspend fun doWork(): Result = delegateWorker.doWork()
}