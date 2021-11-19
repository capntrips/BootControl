package com.github.capntrips.devinfopatcher

internal class MainListener constructor(private val callback: () -> Unit) {
    fun resume() {
        callback.invoke()
    }
}
