package com.github.capntrips.bootcontrol

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    context: Context,
    bootctl: IBootControlService,
    navController: NavHostController,
) : ViewModel() {
    companion object {
        const val TAG: String = "BootControl/MainViewModel"
    }

    val reboot: RebootViewModel

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private lateinit var _uiState: MutableStateFlow<DeviceState>
    private var _error: String? = null

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()
    val uiState: StateFlow<DeviceState>
        get() = _uiState.asStateFlow()
    val hasError: Boolean
        get() = _error != null
    val error: String
        get() = _error ?: "Unknown Error"

    init {
        try {
            _uiState = MutableStateFlow(DeviceState(context, bootctl, _isRefreshing))
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            _error = e.message
        }
        reboot = RebootViewModel(navController, _isRefreshing)
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.emit(true)
            try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                _error = e.message
            }
            _isRefreshing.emit(false)
        }
    }

    fun refresh() {
        launch {
            uiState.value.refresh()
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

    fun activate(context: Context, slot: SlotState, callback: () -> Unit) {
        launch {
            val slotA = uiState.value.slotA.value
            val slotB = uiState.value.slotB.value
            if (slotA == slot) {
                uiState.value.setActiveBootSlot(0)
            } else if (slotB == slot) {
                uiState.value.setActiveBootSlot(1)
            } else {
                log(context, "Invalid slot", shouldThrow = true)
            }
            log(context, "slot activated")
            uiState.value.refresh()
            withContext(Dispatchers.Main) {
                callback.invoke()
            }
        }
    }
}
