package com.itayc.loggers_android.disklogger.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.await
import androidx.work.workDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Sets a [Worker] to delete hte logs
 */
internal class InitCleanLogWorkerIfNeededUseCase(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
) {

    operator fun invoke(appContext: Context, keepLogsPeriod: Int) = scope.launch {
        val workName = "CleanLogsWork"
        val tagPrefix = "CleanLogs"
        val tag = "$tagPrefix-$keepLogsPeriod"
        val workConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        // Use CleanLogsDummyWorker to support simple worker factory. see [CleanLogsDummyWorker]
        val cleanLogsWork = PeriodicWorkRequestBuilder<CleanLogsDummyWorker>(keepLogsPeriod.toLong(), TimeUnit.DAYS)
            .setInputData(workDataOf(CleanLogsWorker.KEEP_LOG_PERIOD_KEY to keepLogsPeriod))
            .setConstraints(workConstraints)
            .addTag(tag)
            .build()
        val workManager = WorkManagerProvider.getInstance(appContext)
        val policy = workManager
            .getWorkInfosForUniqueWork(workName)
            .await()
            .getOrNull(0)
            ?.tags
            ?.find { it.contains(tagPrefix) }
            ?.let { oldTag ->
                if (oldTag == tag)
                    ExistingPeriodicWorkPolicy.KEEP
                else
                    ExistingPeriodicWorkPolicy.UPDATE
            } ?: ExistingPeriodicWorkPolicy.KEEP
        workManager.enqueueUniquePeriodicWork(workName, policy, cleanLogsWork)
    }
}