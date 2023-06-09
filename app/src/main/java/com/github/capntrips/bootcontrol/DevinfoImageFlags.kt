package com.github.capntrips.bootcontrol

enum class DevinfoImageFlags(val position: Byte) {
    Unbootable(1),
    Successful(2),
    Active(4),
    FastbootOk(8),
}