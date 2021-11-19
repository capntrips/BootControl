package com.github.capntrips.devinfopatcher

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface MainViewModelInterface {
    val isRefreshing: StateFlow<Boolean>
    val uiState: StateFlow<DeviceStateInterface>
    fun refresh(context: Context)
}
