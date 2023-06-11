package com.github.capntrips.bootcontrol;

interface IBootControlService {
    String halInfo();
    float halVersion();
    int getNumberSlots();
    int getCurrentSlot();
    boolean setActiveBootSlot(int slot);
    boolean isSlotBootable(int slot);
    boolean isSlotMarkedSuccessful(int slot);
    String getSuffix(int slot);
    int getActiveBootSlot();
}
