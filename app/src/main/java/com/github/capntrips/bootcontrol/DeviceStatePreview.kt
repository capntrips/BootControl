package com.github.capntrips.bootcontrol

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeviceStatePreview constructor(_isRefreshing : MutableStateFlow<Boolean>) : ViewModel(), DeviceStateInterface {
    private var _slotA: MutableStateFlow<SlotStateInterface> = MutableStateFlow(SlotStatePreview(_isRefreshing, false))
    private var _slotB: MutableStateFlow<SlotStateInterface> = MutableStateFlow(SlotStatePreview(_isRefreshing, true))
    override val slotSuffix: String = "_b"

    override val slotA: StateFlow<SlotStateInterface>
        get() = _slotA.asStateFlow()
    override val slotB: StateFlow<SlotStateInterface>
        get() = _slotB.asStateFlow()

    override fun refresh(context: Context) {
        slotA.value.refresh(context)
        slotB.value.refresh(context)
    }
}
