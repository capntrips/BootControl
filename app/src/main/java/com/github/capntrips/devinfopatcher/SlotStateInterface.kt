package com.github.capntrips.devinfopatcher

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface SlotStateInterface {
    var retryCount: Int
    var unbootable: Boolean
    var successful: Boolean
    var active: Boolean
    var fastbootOk: Boolean
    val isRefreshing: StateFlow<Boolean>
    fun refresh(context: Context)
    fun patch(context: Context)
}
