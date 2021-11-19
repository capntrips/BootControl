package com.github.capntrips.devinfopatcher

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface DeviceStateInterface {
    val slotSuffix: String
    val slotA: StateFlow<SlotStateInterface>
    val slotB: StateFlow<SlotStateInterface>
    fun refresh(context: Context)
}
