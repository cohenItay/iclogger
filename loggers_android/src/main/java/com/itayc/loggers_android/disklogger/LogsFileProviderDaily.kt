package com.itayc.loggers_android.disklogger

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Provide the the directory and the file for the logs to be written to as long as we are in the same day.
 * day is defined by the [locale] being sent
 * @param locale - the [Locale] required.
 */
class LogsFileProviderDaily(
    private val locale: Locale
) : LogsFileProvider {

    private val TAG = LogsFileProvider::class.simpleName!!
    private val folderNameFormat = SimpleDateFormat("dd_MM_yyyy", locale)

    init {
        // If the client has decided to use this file provider we will instantiate the
    }

    override suspend fun provideLogsDirectory(appContext: Context) =
        File(("${appContext.filesDir}/logs"))

    override suspend fun provideFile(appContext: Context): File? {
        val fileName = folderNameFormat.format(Calendar.getInstance(locale).time)
        return getFileForToday(appContext, fileName)
    }

    private suspend fun getFileForToday(appContext: Context, fileName: String): File? = coroutineScope {
        val folder = provideLogsDirectory(appContext)
        if (!folder.exists() && !folder.mkdir())
            return@coroutineScope null
        val file = File(folder, "$fileName.log")
        return@coroutineScope if (!file.exists()) {
            withContext(Dispatchers.IO) {
                try {
                    if (file.createNewFile()) file else null
                } catch (e: IOException) {
                    Log.e(TAG, "Problem creating the App log file", e)
                    null
                }
            }
        } else {
            file
        }
    }
}
