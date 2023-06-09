package com.github.capntrips.bootcontrol

internal class MainListener constructor(private val callback: () -> Unit) {
    fun resume() {
        callback.invoke()
    }
}
