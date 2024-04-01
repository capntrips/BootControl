package com.github.capntrips.bootcontrol

import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.topjohnwu.superuser.ipc.RootService

class BootControlService : RootService() {
    inner class BootControlIPC : IBootControlService.Stub() {
        init {
            if (Process.myUid() == 0) {
                System.loadLibrary("bootcontrol")
            }
        }

        external override fun halInfo(): String
        external override fun halVersion(): Int
        external override fun getNumberSlots(): Int
        external override fun getCurrentSlot(): Int
        external override fun setActiveBootSlot(slot: Int): CommandResult
        external override fun isSlotBootable(slot: Int): BoolResult
        external override fun isSlotMarkedSuccessful(slot: Int): BoolResult
        external override fun getSuffix(slot: Int): String?
        external override fun getActiveBootSlot(): Int
    }

    override fun onBind(intent: Intent): IBinder {
        return BootControlIPC()
    }
}
