package com.github.capntrips.bootcontrol

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DeviceState(
    context: Context,
    private val bootctl: IBootControlService,
    private val _isRefreshing : MutableStateFlow<Boolean>
) : ViewModel() {
    companion object {
        const val TAG: String = "BootControl/BootControl"
    }

    private var _slotA: MutableStateFlow<SlotState>
    private var _slotB: MutableStateFlow<SlotState>
    val halInfo: String
    val halVersion: BootControlVersion
    val slotSuffix: String
    var initialized: Boolean = false
        private set

    val slotA: StateFlow<SlotState>
        get() = _slotA.asStateFlow()
    val slotB: StateFlow<SlotState>
        get() = _slotB.asStateFlow()

    private fun _refresh() {
        slotA.value.unbootable = !isSlotBootable(0)
        slotA.value.successful = isSlotMarkedSuccessful(0)
        slotB.value.unbootable = !isSlotBootable(1)
        slotB.value.successful = isSlotMarkedSuccessful(1)
        if (halVersion >= BootControlVersion.BOOTCTL_V1_2) {
            val activeSlot = getActiveBootSlot()
            slotA.value.active = activeSlot == 0
            slotB.value.active = activeSlot == 1
        }
    }

    fun refresh() {
        launch {
            _refresh()
        }
    }

    init {
        _slotA = MutableStateFlow(SlotState())
        _slotB = MutableStateFlow(SlotState())
        halInfo = halInfo()
        halVersion = halVersion()
        slotSuffix = getSuffix(getCurrentSlot())
        _refresh()
        initialized = true
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

    private fun halInfo(): String {
        return bootctl.halInfo()
    }

    private fun halVersion(): BootControlVersion {
        return BootControlVersion.entries[bootctl.halVersion()]
    }

    private fun getCurrentSlot(): Int {
        return bootctl.getCurrentSlot()
    }

    fun setActiveBootSlot(slot: Int): Boolean {
        return bootctl.setActiveBootSlot(slot).success
    }

    private fun isSlotBootable(slot: Int): Boolean {
        return bootctl.isSlotBootable(slot).value
    }

    private fun isSlotMarkedSuccessful(slot: Int): Boolean {
        return bootctl.isSlotMarkedSuccessful(slot).value
    }

    private fun getSuffix(slot: Int): String {
        return bootctl.getSuffix(slot)
    }

    private fun getActiveBootSlot(): Int {
        return bootctl.getActiveBootSlot()
    }
}
