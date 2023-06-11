package android.hidl.base.V1_0;


public final class DebugInfo {
    public static final class Architecture {
        public static final int UNKNOWN = 0;
        public static final int IS_64BIT = 1 /* ::android::hidl::base::V1_0::DebugInfo::Architecture.UNKNOWN implicitly + 1 */;
        public static final int IS_32BIT = 2 /* ::android::hidl::base::V1_0::DebugInfo::Architecture.IS_64BIT implicitly + 1 */;
        public static final String toString(int o) {
            if (o == UNKNOWN) {
                return "UNKNOWN";
            }
            if (o == IS_64BIT) {
                return "IS_64BIT";
            }
            if (o == IS_32BIT) {
                return "IS_32BIT";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            java.util.ArrayList<String> list = new java.util.ArrayList<>();
            int flipped = 0;
            list.add("UNKNOWN"); // UNKNOWN == 0
            if ((o & IS_64BIT) == IS_64BIT) {
                list.add("IS_64BIT");
                flipped |= IS_64BIT;
            }
            if ((o & IS_32BIT) == IS_32BIT) {
                list.add("IS_32BIT");
                flipped |= IS_32BIT;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString(o & (~flipped)));
            }
            return String.join(" | ", list);
        }

    };

    public int pid = 0;
    public long ptr = 0L;
    public int arch = 0;

    @Override
    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        if (otherObject.getClass() != android.hidl.base.V1_0.DebugInfo.class) {
            return false;
        }
        android.hidl.base.V1_0.DebugInfo other = (android.hidl.base.V1_0.DebugInfo)otherObject;
        if (this.pid != other.pid) {
            return false;
        }
        if (this.ptr != other.ptr) {
            return false;
        }
        if (this.arch != other.arch) {
            return false;
        }
        return true;
    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hash(
                android.os.HidlSupport.deepHashCode(this.pid), 
                android.os.HidlSupport.deepHashCode(this.ptr), 
                android.os.HidlSupport.deepHashCode(this.arch));
    }

    @Override
    public final String toString() {
        java.lang.StringBuilder builder = new java.lang.StringBuilder();
        builder.append("{");
        builder.append(".pid = ");
        builder.append(this.pid);
        builder.append(", .ptr = ");
        builder.append(this.ptr);
        builder.append(", .arch = ");
        builder.append(android.hidl.base.V1_0.DebugInfo.Architecture.toString(this.arch));
        builder.append("}");
        return builder.toString();
    }

    public final void readFromParcel(android.os.HwParcel parcel) {
        android.os.HwBlob blob = parcel.readBuffer(24 /* size */);
        readEmbeddedFromParcel(parcel, blob, 0 /* parentOffset */);
    }

    public static final java.util.ArrayList<DebugInfo> readVectorFromParcel(android.os.HwParcel parcel) {
        java.util.ArrayList<DebugInfo> _hidl_vec = new java.util.ArrayList();
        android.os.HwBlob _hidl_blob = parcel.readBuffer(16 /* sizeof hidl_vec<T> */);

        {
            int _hidl_vec_size = _hidl_blob.getInt32(0 + 8 /* offsetof(hidl_vec<T>, mSize) */);
            android.os.HwBlob childBlob = parcel.readEmbeddedBuffer(
                    _hidl_vec_size * 24,_hidl_blob.handle(),
                    0 + 0 /* offsetof(hidl_vec<T>, mBuffer) */,true /* nullable */);

            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; ++_hidl_index_0) {
                android.hidl.base.V1_0.DebugInfo _hidl_vec_element = new android.hidl.base.V1_0.DebugInfo();
                ((android.hidl.base.V1_0.DebugInfo) _hidl_vec_element).readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 24);
                _hidl_vec.add(_hidl_vec_element);
            }
        }

        return _hidl_vec;
    }

    public final void readEmbeddedFromParcel(
            android.os.HwParcel parcel, android.os.HwBlob _hidl_blob, long _hidl_offset) {
        pid = _hidl_blob.getInt32(_hidl_offset + 0);
        ptr = _hidl_blob.getInt64(_hidl_offset + 8);
        arch = _hidl_blob.getInt32(_hidl_offset + 16);
    }

    public final void writeToParcel(android.os.HwParcel parcel) {
        android.os.HwBlob _hidl_blob = new android.os.HwBlob(24 /* size */);
        writeEmbeddedToBlob(_hidl_blob, 0 /* parentOffset */);
        parcel.writeBuffer(_hidl_blob);
    }

    public static final void writeVectorToParcel(
            android.os.HwParcel parcel, java.util.ArrayList<DebugInfo> _hidl_vec) {
        android.os.HwBlob _hidl_blob = new android.os.HwBlob(16 /* sizeof(hidl_vec<T>) */);
        {
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(0 + 8 /* offsetof(hidl_vec<T>, mSize) */, _hidl_vec_size);
            _hidl_blob.putBool(0 + 12 /* offsetof(hidl_vec<T>, mOwnsBuffer) */, false);
            android.os.HwBlob childBlob = new android.os.HwBlob((int)(_hidl_vec_size * 24));
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; ++_hidl_index_0) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 24);
            }
            _hidl_blob.putBlob(0 + 0 /* offsetof(hidl_vec<T>, mBuffer) */, childBlob);
        }

        parcel.writeBuffer(_hidl_blob);
    }

    public final void writeEmbeddedToBlob(
            android.os.HwBlob _hidl_blob, long _hidl_offset) {
        _hidl_blob.putInt32(_hidl_offset + 0, pid);
        _hidl_blob.putInt64(_hidl_offset + 8, ptr);
        _hidl_blob.putInt32(_hidl_offset + 16, arch);
    }
};

