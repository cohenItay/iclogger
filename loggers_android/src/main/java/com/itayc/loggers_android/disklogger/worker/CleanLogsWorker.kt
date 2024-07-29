package com.itayc.loggers_android.disklogger.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.itayc.loggers_android.disklogger.DiskLogger
import java.util.Calendar
import java.util.Locale

class CleanLogsWorker private constructor(
    appContext: Context,
    workerParams: WorkerParameters,
    private val diskLogger: DiskLogger,
    private val locale: Locale
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val keepLogsPeriod = inputData.getInt(KEEP_LOG_PERIOD_KEY, -1)
        val outputData = workDataOf(KEEP_LOG_PERIOD_KEY to keepLogsPeriod)
        return if (keepLogsPeriod == -1)
             Result.failure(outputData)
        else try {
            val dateToRemoveFrom = Calendar.getInstance(locale).let { calendar ->
                calendar.add(Calendar.DAY_OF_YEAR, -1*(keepLogsPeriod+1))
                calendar.time
            }
            diskLogger.cleanLogs(dateToRemoveFrom)
            Result.success(outputData)
        } catch (e: IllegalStateException) {
            Log.e("CleanLogsWorker", "doWork: Cannot clean logs", e)
            Result.failure(outputData)
        }
    }

    class Factory constructor(
        private val diskLogger: DiskLogger,
        private val locale: Locale
    ) {

        fun create(
            appContext: Context,
            workerParameters: WorkerParameters,
        ) = CleanLogsWorker(appContext, workerParameters, diskLogger, locale)
    }

    companion object {
        const val KEEP_LOG_PERIOD_KEY = "keepLogKkeeyy"
    }
}