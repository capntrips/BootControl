package com.github.capntrips.bootcontrol

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RebootViewModel(
    private val navController: NavController,
    private val _isRefreshing : MutableStateFlow<Boolean>
) : ViewModel() {
    companion object {
        const val TAG: String = "KernelFlasher/RebootState"
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                block()
            } catch (e: Exception) {
                withContext (Dispatchers.Main) {
                    Log.e(TAG, e.message, e)
                    navController.navigate("error/${e.message}") {
                        popUpTo("main")
                    }
                }
            }
            _isRefreshing.value = false
        }
    }

    private fun reboot(destination: String = "") {
        launch {
            // https://github.com/topjohnwu/Magisk/blob/v25.2/app/src/main/java/com/topjohnwu/magisk/ktx/XSU.kt#L11-L15
            if (destination == "recovery") {
                // https://github.com/topjohnwu/Magisk/pull/5637
                Shell.cmd("/system/bin/input keyevent 26").submit()
            }
            Shell.cmd("/system/bin/svc power reboot $destination || /system/bin/reboot $destination").submit()
        }
    }

    fun rebootSystem() {
        reboot()
    }

    fun rebootUserspace() {
        reboot("userspace")
    }

    fun rebootRecovery() {
        reboot("recovery")
    }

    fun rebootBootloader() {
        reboot("bootloader")
    }

    fun rebootDownload() {
        reboot("download")
    }

    fun rebootEdl() {
        reboot("edl")
    }
}
