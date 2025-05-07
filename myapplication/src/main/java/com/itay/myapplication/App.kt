package com.itay.myapplication

import android.app.Application
import com.itayc.iclogger.AllowLogs
import com.itayc.iclogger.LogLevel
import com.itayc.iclogger.LoggerIdOwner
import com.itayc.iclogger.createIcLoggerInstance
import com.itayc.loggers_android.AndroidLogcatLoggerAdapter
import com.itayc.loggers_android.disklogger.DiskLoggerBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        val dl = object: LoggerIdOwner {
            override val id: String
                get() = "dl"
        }
        val icLogger = createIcLoggerInstance(
            consoleLogger = AndroidLogcatLoggerAdapter(),
            allowLogsInitial = AllowLogs.Yes(LogLevel.DEBUG),
            loggersMap = mapOf(
                dl.id to DiskLoggerBuilder.buildWith(applicationContext, Dispatchers.IO) {  }
            )
        )
        CoroutineScope(Dispatchers.IO).launch {
            launch(start = CoroutineStart.UNDISPATCHED) {
                repeat(10_000) {
                    icLogger.d("TAG", "test", dl)
                }
            }
            delay(30)
            error("Hahahhaahhaha!!!!")
        }
    }
}