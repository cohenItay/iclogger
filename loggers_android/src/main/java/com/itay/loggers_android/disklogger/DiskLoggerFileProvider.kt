package com.itay.loggers_android.disklogger

import androidx.core.content.FileProvider

/*
    A dummy class that overrides androidx's file provider in order to avoid Manifest merge collisions
    in cases when the client app will declare "androidx.core.content.FileProvider"
 */
internal class DiskLoggerFileProvider : FileProvider()