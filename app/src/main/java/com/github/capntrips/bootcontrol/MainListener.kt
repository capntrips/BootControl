package com.github.capntrips.bootcontrol

internal class MainListener(private val callback: () -> Unit) {
    fun resume() {
        callback.invoke()
    }
}
