package com.github.capntrips.bootcontrol

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModelPreview : ViewModel(), MainViewModelInterface {
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var _uiState: MutableStateFlow<DeviceStateInterface> = MutableStateFlow(DeviceStatePreview(_isRefreshing))

    override val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()
    override val uiState: StateFlow<DeviceStateInterface>
        get() = _uiState.asStateFlow()

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            block()
            _isRefreshing.emit(false)
        }
    }

    override fun refresh(context: Context) {
        launch {
            delay(500)
            uiState.value.refresh(context)
        }
    }

    override fun activate(context: Context, slot: SlotStateInterface) {
        launch {
            delay(500)
            val slotA = uiState.value.slotA.value
            val slotB = uiState.value.slotB.value
            slotA.setActive(context, slotA == slot)
            slotB.setActive(context, slotB == slot)
        }
    }
}
