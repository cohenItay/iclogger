package com.itayc.loggers_android.disklogger

import android.util.Log
import com.itayc.iclogger.LogLevel

internal class UncaughtCrashHandlerDisk(private val diskLogger: DiskLogger) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        (diskLogger as? ImmediateLogging)?.immediateWriteLog(
            buildString {
                appendLine("=== Crash Detected ===")
                appendLine("Thread: ${thread.name}")
                appendLine("Exception: ${throwable::class.java.name}")
                appendLine("Message: ${throwable.message}")
                appendLine("Stacktrace:")
                appendLine(Log.getStackTraceString(throwable))
                appendLine("======================")
            }
        )
        diskLogger.releaseResources()
        // Always pass it on to the default handler (to let system crash the app)
        defaultHandler?.uncaughtException(thread, throwable)
    }
}