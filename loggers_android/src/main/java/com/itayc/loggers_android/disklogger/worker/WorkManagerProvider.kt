package com.itayc.loggers_android.disklogger.worker

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

internal object WorkManagerProvider {

    private val TAG = WorkManagerProvider::class.simpleName!!

    fun getInstance(appContext : Context, isDebug: Boolean = false) : WorkManager  = try {
        Log.d(TAG, "setting up work-manager")
        // will try to get work-manager instance if it is not initialised, will try to initialise it
        WorkManager.getInstance(appContext)
    } catch (e: IllegalStateException) {
        Log.d(TAG, "initialising work-manager ${e.message}")
        initialiseWorkManager(appContext, isDebug)
        WorkManager.getInstance(appContext)
    }


    private fun initialiseWorkManager(context: Context, isDebug: Boolean) {
        try {
            WorkManager.initialize(
                context,
                Configuration.Builder()
                    .setMinimumLoggingLevel(if (isDebug) Log.DEBUG else Log.INFO)
                    .build()
            )
            Log.d(TAG , "work-manager initialised")
        } catch (e : IllegalStateException) {
            Log.d(TAG , "work-manager already initialised by clients application")
        }
    }
}