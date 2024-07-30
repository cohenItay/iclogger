# ICLogger
### A decorator pattern logger written in kotlin

### Background
Due to my expirience in Android development, one of bottelnecks which I had along the way was that `Log.d(String, String)` was all over place: In UI / Domain layer - Logic classes / Data Layer. 

Does my classes should be depended on Android SDK? No, it should live and work as long it is in Kotlin/Java
The problem comes from `Log.x` belongs to the `android.util` SDK which creates a strong dependency between the business/domain layer classes and the android SDK. 
Thinking about it in a broader way that is true for any logger which is not owned by the business. 

#### what problems does it solves? 
1. Separation from interacting (directly) with 3rd party loggers
2. Being able to replace the entire system / application logger with ease

## How to use? 
first we need to add the library into our project, it is a maven package and can be added to your `.pom` file or using gradle:
```
implementation("com.itayc:iclogger:1.1") // the core files
implementation("com.itayc:iclogger-android-loggers:1.1") // extra logger implementations for android platform
```

Once added you will have a the possibility to create an instance of `ICLogger`
```
val iclogger = createIcloggerInstance(
  consoleLogger: Logger?,
  isInDebug: Boolean,
  loggersMap: Map<String, Logger>
)
```
* The `consoleLogger` will be the logger which prints the logs onto the terminal window of your system,
for Android it would be the logcat, you can use the `AndroidLogcatLoggerAdapter()` from the `android-loggers` lib.
* `isInDebug` basically enabled / disables the logger - once off debug mode don't log anything.
* `loggersMap` are pairs of id-to-logger, the id identifies the logger itself. those extra loggers will be logging the log in addition to the `consoleLogger`

#### Play time
Now just use it:
```
iclogger.d("myTag", "myLog")
iclogger.e("myTag", "myLog", MyExecption("Exception has occured"))
iclogger.e("myTag", "myLog", MyExecption("Exception has occured"), DiskLogger)
iclogger.i("myTag", "important log", , AnalyticsLogger, DiskLogger)
```

Noticed the `AnalyticsLogger` or the `DiskLogger` - that is how simple it can be sending the same log to different places.
You can implement your own loggers or use the provided Android loggers in the lib.
The `android-loggers` lib provides the following loggers:
1. AndroidLogcatLoggerAdapter - the console logger for android
2. DiskLogger - a logger which can save logs into the internal storage of your android application for a defined time period. then the user can share them with ease

##### DiskLogger
to create an instance of it use its builder:
```
DiskLoggerBuilder.buildWith(appContext, dispatcherIo) {
    keepLogsPeriod = KEEP_LOGS_PERIOD
    locale = appContext.resources.configuration.locales[0]
}
```
Don't forget to call `icLogger.releaseResources()` on application/system exit because if for example it uses the `DiskLogger` then we would like to persist all of the log within the buffer into the disk. 

