package com.itay.loggers_android.disklogger

import java.io.File
import java.util.Stack

object FileUtils {

    @JvmStatic
    fun getAllFilesInDir(dir: File): List<File> {
        val files = mutableListOf<File>()
        val dirStack = Stack<File>()
        dirStack.clear()
        dirStack.push(dir)
        while (!dirStack.isEmpty()) {
            val dirCurrent: File = dirStack.pop()
            dirCurrent.listFiles()?.also { fileList ->
                for (aFileList in fileList) {
                    if (aFileList.isDirectory) dirStack.push(aFileList) else files.add(aFileList)
                }
            }
        }
        return files
    }
}