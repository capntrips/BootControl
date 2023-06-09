package com.github.capntrips.bootcontrol

import android.content.Context
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class DeviceState constructor(context: Context, _isRefreshing : MutableStateFlow<Boolean>) : DeviceStateInterface {
    private var _slotA: MutableStateFlow<SlotStateInterface>
    private var _slotB: MutableStateFlow<SlotStateInterface>
    override val slotSuffix: String

    override val slotA: StateFlow<SlotStateInterface>
        get() = _slotA.asStateFlow()
    override val slotB: StateFlow<SlotStateInterface>
        get() = _slotB.asStateFlow()

    override fun refresh(context: Context) {
        slotA.value.refresh(context)
        slotB.value.refresh(context)
    }

    init {
        // https://android.googlesource.com/platform/system/update_engine/+/refs/tags/android-12.0.0_r12/aosp/dynamic_partition_control_android.cc#393
        // https://android.googlesource.com/platform/system/core/+/refs/tags/android-12.0.0_r12/fs_mgr/fs_mgr_fstab.cpp#416
        // https://android.googlesource.com/platform/system/core/+/refs/tags/android-12.0.0_r12/fs_mgr/fs_mgr_boot_config.cpp#156
        val hardwarePlatform = Shell.su("getprop ro.boot.hardware.platform").exec().out[0]
        val misc = File(Shell.su("cat /vendor/etc/fstab.$hardwarePlatform | grep /misc | awk '{ print \$1 }'").exec().out[0])
        val devinfo = misc.resolveSibling("devinfo")

        // https://android.googlesource.com/device/google/gs101/+/refs/tags/android-12.0.0_r12/interfaces/boot/1.2/BootControl.cpp#194
        slotSuffix = Shell.su("getprop ro.boot.slot_suffix").exec().out[0]

        _slotA = MutableStateFlow(SlotState(context, devinfo, 48, _isRefreshing))
        _slotB = MutableStateFlow(SlotState(context, devinfo, 52, _isRefreshing))
    }
}
