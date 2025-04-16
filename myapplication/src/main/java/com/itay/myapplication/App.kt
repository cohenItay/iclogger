package com.itay.myapplication

import android.app.Application
import com.itayc.iclogger.AllowLogs
import com.itayc.iclogger.LogLevel
import com.itayc.iclogger.createIcLoggerInstance
import com.itayc.loggers_android.AndroidLogcatLoggerAdapter
import com.itayc.loggers_android.disklogger.DiskLoggerBuilder
import kotlinx.coroutines.Dispatchers

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val icLogger = createIcLoggerInstance(
            consoleLogger = AndroidLogcatLoggerAdapter(),
            allowLogsInitial = AllowLogs.Yes(LogLevel.DEBUG),
            loggersMap = mapOf(
                "id" to DiskLoggerBuilder.buildWith(applicationContext, Dispatchers.IO) {  }
            )
        )
        icLogger.d("TAG", "test")
        throw RuntimeException("Test crash")
    }
}