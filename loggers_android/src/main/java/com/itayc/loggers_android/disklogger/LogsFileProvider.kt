package com.itayc.loggers_android.disklogger

import android.content.Context
import java.io.File

/**
 * Provide the the directory and the file for the logs to be written to.
 */
interface LogsFileProvider {

    /**
     * @return [File] directory in which the logs file will be saved in.
     */
    suspend fun provideLogsDirectory(appContext: Context): File

    /**
     * @return [File] of type .log, which you can write logs to it.
     */
    suspend fun provideFile(appContext: Context): File?
}
