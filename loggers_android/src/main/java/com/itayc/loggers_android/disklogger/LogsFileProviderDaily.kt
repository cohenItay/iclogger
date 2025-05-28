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
import java.util.TimeZone

/**
 * Provide the the directory and the file for the logs to be written to as long as we are in the same day.
 * day is defined by the [locale] being sent
 * @param locale - the [Locale] required.
 * @param relativePath write the logs into .../logs/<your_relative_path>, null when will use the log directory
 */
class LogsFileProviderDaily(
    private val locale: Locale,
    private val timeZone: TimeZone,
    relativePath: String? = null
) : LogsFileProvider {

    private val TAG = LogsFileProvider::class.simpleName!!
    private val folderNameFormat = SimpleDateFormat("dd_MM_yyyy", locale).also {
        it.timeZone = timeZone
    }
    private val logsRelativeDir = buildString {
        append("/logs")
        relativePath?.let {
            val noSlashes = it.trim('/', ' ')
            append("/$noSlashes")
        }
    }

    override suspend fun provideLogsDirectory(appContext: Context) =
        File(("${appContext.filesDir}$logsRelativeDir"))

    override suspend fun provideFile(appContext: Context): File? {
        val fileName = folderNameFormat.format(Calendar.getInstance(timeZone, locale).time)
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
