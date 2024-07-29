package com.itay.loggers_android.disklogger

import com.itay.iclogger.LogLevel
import com.itay.iclogger.Logger
import java.util.Date

/**
 * Helps writing logs into the disk.
 * see more at https://medium.com/@itay.c14/disk-logs-simple-android-logger-778566726a76
 */
interface DiskLogger : Logger {

    /**
     * Log the [message] to a file on disk, any log written will be prefixed with a timestamp
     */
    override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?)

    /**
     * Clean any logs files which are kept on the disk.
     * @param from - the specified time which from this day and onto the past they will be deleted,
     * must be at least from yesterday
     * @throws IllegalStateException if the [from] days is less then 1 day
     */
    fun cleanLogs(from: Date)

    /**
     * Flush any pending logs into the disk if there are any
     */
    suspend fun flushToDisk()
}