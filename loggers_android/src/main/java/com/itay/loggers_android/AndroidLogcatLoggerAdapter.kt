package com.itay.loggers_android

import android.util.Log
import com.itay.iclogger.LogLevel
import com.itay.iclogger.Logger
import com.itay.iclogger.appendAttrsToLog

class AndroidLogcatLoggerAdapter : Logger {

    override fun log(tag: String, logLevel: LogLevel, message: String, throwable: Throwable?, attributes: Map<String, Any?>?) {
        val withAttr = message.appendAttrsToLog(attributes)
        when (logLevel) {
            LogLevel.DEBUG -> Log.d(tag, withAttr, throwable)
            LogLevel.ERROR -> Log.e(tag, withAttr, throwable)
            LogLevel.INFO -> Log.i(tag, withAttr, throwable)
            LogLevel.WARNING -> Log.w(tag, withAttr, throwable)
        }
    }

    override suspend fun releaseResources() {}
}