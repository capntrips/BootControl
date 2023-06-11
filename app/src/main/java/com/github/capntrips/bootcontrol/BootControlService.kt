package com.github.capntrips.bootcontrol

import android.content.Intent
import android.hardware.boot.V1_0.BoolResult
import android.hardware.boot.V1_0.IBootControl as IBootControlV1_0
import android.hardware.boot.V1_1.IBootControl as IBootControlV1_1
import android.hardware.boot.V1_2.IBootControl as IBootControlV1_2
import android.os.IBinder
import android.util.Log
import com.topjohnwu.superuser.ipc.RootService

class BootControlService : RootService() {
    companion object {
        const val TAG: String = "BootControlService"
    }

    inner class BootControlIPC : IBootControlService.Stub() {
        private val serviceV1_0 = IBootControlV1_0.getService(true)
        private val serviceV1_1 = IBootControlV1_1.castFrom(serviceV1_0)
        private val serviceV1_2 = IBootControlV1_2.castFrom(serviceV1_0)

        private val service: IBootControlV1_0
            get() = serviceV1_2 ?: (serviceV1_1 ?: serviceV1_0)

        override fun halInfo(): String {
            return "HAL Version: ${service.interfaceDescriptor()}"
        }

        override fun halVersion(): Float {
            return if (serviceV1_2 != null) 1.2f else if (serviceV1_1 != null) 1.1f else 1.0f
        }

        override fun getNumberSlots(): Int {
            return service.numberSlots
        }

        override fun getCurrentSlot(): Int {
            return service.currentSlot
        }

        override fun setActiveBootSlot(slot: Int): Boolean {
            val result = service.setActiveBootSlot(slot)
            if (!result.success) {
                Log.e(TAG, "Error setting active boot slot: ${result.errMsg}")
            }
            return result.success
        }

        override fun isSlotBootable(slot: Int): Boolean {
            return service.isSlotBootable(slot) == BoolResult.TRUE
        }

        override fun isSlotMarkedSuccessful(slot: Int): Boolean {
            return service.isSlotMarkedSuccessful(slot) == BoolResult.TRUE
        }

        override fun getSuffix(slot: Int): String {
            return service.getSuffix(slot)
        }

        override fun getActiveBootSlot(): Int {
            return serviceV1_2.activeBootSlot
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return BootControlIPC()
    }
}
