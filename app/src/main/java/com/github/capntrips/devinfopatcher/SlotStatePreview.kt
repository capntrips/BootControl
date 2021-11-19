package com.github.capntrips.devinfopatcher

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SlotStatePreview constructor(private val _isRefreshing : MutableStateFlow<Boolean>, isActive: Boolean) : ViewModel(), SlotStateInterface {
    override var retryCount: Int = if (isActive) 2 else 3
    override var unbootable: Boolean = false
    override var successful: Boolean = isActive
    override var active: Boolean = isActive
    override var fastbootOk: Boolean = isActive

    override val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

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
        }
    }

    override fun patch(context: Context) {
        launch {
            delay(500)
            successful = true
        }
    }
}
