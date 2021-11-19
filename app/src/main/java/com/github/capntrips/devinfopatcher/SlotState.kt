package com.github.capntrips.devinfopatcher

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.topjohnwu.superuser.Shell
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.properties.Delegates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SlotState(context: Context, private val devinfo: File, private val offset: Int, private val _isRefreshing : MutableStateFlow<Boolean>) : ViewModel(), SlotStateInterface {
    companion object {
        const val TAG: String = "VbmetaPatcher/SlotState"
        const val MAGIC: String = "49564544"
    }

    override var retryCount: Int by Delegates.notNull()
    override var unbootable: Boolean by Delegates.notNull()
    override var successful: Boolean by Delegates.notNull()
    override var active: Boolean by Delegates.notNull()
    override var fastbootOk: Boolean by Delegates.notNull()

    override val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

    private fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

    init {
        refresh(context)
    }

    override fun refresh(context: Context) {
        if (!hasMagic()) {
            log(context, "Unexpected Format", shouldThrow = true)
        }
        val data = Base64.decode(Shell.su("dd if=$devinfo bs=1 count=128 status=none | base64 -w 0").exec().out[0], Base64.DEFAULT)
        val buffer = ByteBuffer.wrap(data)

        buffer.order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(offset)

        retryCount = buffer.get().toInt()
        val flags = buffer.get()
        unbootable = flags and DevinfoImageFlags.Unbootable.position != 0.toByte()
        successful = flags and DevinfoImageFlags.Successful.position != 0.toByte()
        active = flags and DevinfoImageFlags.Active.position != 0.toByte()
        fastbootOk = flags and DevinfoImageFlags.FastbootOk.position != 0.toByte()
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.emit(true)
            block()
            _isRefreshing.emit(false)
        }
    }

    private fun log(context: Context, message: String, shouldThrow: Boolean = false) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        Log.d(TAG, message)
        if (shouldThrow) {
            throw Exception(message)
        }
    }

    override fun patch(context: Context) {
        launch {
            setSuccessful(context)
            log(context, "slot patched")
        }
    }

    private fun setSuccessful(context: Context) {
        if (!hasMagic()) {
            log(context, "Unexpected Format", shouldThrow = true)
        }

        var flags: Byte = 0x00.toByte()
        // if (unbootable) {
        //     flags = flags or DevinfoImageFlags.Unbootable.position
        // }
        flags = flags or DevinfoImageFlags.Successful.position
        if (active) {
            flags = flags or DevinfoImageFlags.Active.position
        }
        if (fastbootOk) {
            flags = flags or DevinfoImageFlags.FastbootOk.position
        }
        Shell.su("printf '\\x${"%02x".format(flags)}' | dd of=$devinfo bs=1 seek=${offset + 1} count=1 conv=notrunc status=none").exec()
        refresh(context)
    }

    private fun hasMagic() : Boolean {
        // https://android.googlesource.com/device/google/gs101/+/refs/tags/android-12.0.0_r12/interfaces/boot/1.2/DevInfo.h#25
        val magic = Base64.decode(Shell.su("dd if=$devinfo bs=1 count=4 status=none | base64").exec().out[0], Base64.DEFAULT)
        magic.reverse()
        return magic.toHex() == MAGIC
    }
}
