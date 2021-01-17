package com.homework.ninedt.data.utils

import java.util.concurrent.Executors

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

// Runs the function on a background thread dedicated for I/O (i.e. database R/W)
fun ioThread(function: () -> Unit) {
    IO_EXECUTOR.execute(function)
}