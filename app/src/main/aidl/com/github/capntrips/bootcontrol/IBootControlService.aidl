package com.github.capntrips.bootcontrol;

import com.github.capntrips.bootcontrol.BoolResult;
import com.github.capntrips.bootcontrol.CommandResult;

interface IBootControlService {
    String halInfo();
    int halVersion();
    int getNumberSlots();
    int getCurrentSlot();
    CommandResult setActiveBootSlot(int slot);
    BoolResult isSlotBootable(int slot);
    BoolResult isSlotMarkedSuccessful(int slot);
    String getSuffix(int slot);
    int getActiveBootSlot();
}
