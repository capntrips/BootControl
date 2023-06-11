package android.hardware.boot.V1_1;


public final class MergeStatus {
    /**
     * No snapshot or merge is in progress.
     */
    public static final int NONE = 0;
    /**
     * The merge status could not be determined.
     */
    public static final int UNKNOWN = 1 /* ::android::hardware::boot::V1_1::MergeStatus.NONE implicitly + 1 */;
    /**
     * Partitions are being snapshotted, but no merge has been started.
     */
    public static final int SNAPSHOTTED = 2 /* ::android::hardware::boot::V1_1::MergeStatus.UNKNOWN implicitly + 1 */;
    /**
     * At least one partition has merge is in progress.
     */
    public static final int MERGING = 3 /* ::android::hardware::boot::V1_1::MergeStatus.SNAPSHOTTED implicitly + 1 */;
    /**
     * A merge was in progress, but it was canceled by the bootloader.
     */
    public static final int CANCELLED = 4 /* ::android::hardware::boot::V1_1::MergeStatus.MERGING implicitly + 1 */;
    public static final String toString(int o) {
        if (o == NONE) {
            return "NONE";
        }
        if (o == UNKNOWN) {
            return "UNKNOWN";
        }
        if (o == SNAPSHOTTED) {
            return "SNAPSHOTTED";
        }
        if (o == MERGING) {
            return "MERGING";
        }
        if (o == CANCELLED) {
            return "CANCELLED";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        int flipped = 0;
        list.add("NONE"); // NONE == 0
        if ((o & UNKNOWN) == UNKNOWN) {
            list.add("UNKNOWN");
            flipped |= UNKNOWN;
        }
        if ((o & SNAPSHOTTED) == SNAPSHOTTED) {
            list.add("SNAPSHOTTED");
            flipped |= SNAPSHOTTED;
        }
        if ((o & MERGING) == MERGING) {
            list.add("MERGING");
            flipped |= MERGING;
        }
        if ((o & CANCELLED) == CANCELLED) {
            list.add("CANCELLED");
            flipped |= CANCELLED;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(o & (~flipped)));
        }
        return String.join(" | ", list);
    }

};

