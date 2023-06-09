# Boot Control

Boot Control is an Android app that toggles the active slot. It first attempts to modify the `devinfo` partition, if a valid one is found, then falls back on modifying the partition table entries of the `boot` partitions. 

## Usage

Pressing the `Activate` button on the inactive slot will toggle the active slot.
