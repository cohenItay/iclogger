package com.itay.iclogger

interface TagOwner {
    val TAG: String
        get() = this::class.simpleName!!
}