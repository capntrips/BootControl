package android.hardware.boot.V1_0;


public final class BoolResult {
    public static final int FALSE = 0;
    public static final int TRUE = 1;
    public static final int INVALID_SLOT = -1 /* -1 */;
    public static final String toString(int o) {
        if (o == FALSE) {
            return "FALSE";
        }
        if (o == TRUE) {
            return "TRUE";
        }
        if (o == INVALID_SLOT) {
            return "INVALID_SLOT";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        int flipped = 0;
        list.add("FALSE"); // FALSE == 0
        if ((o & TRUE) == TRUE) {
            list.add("TRUE");
            flipped |= TRUE;
        }
        if ((o & INVALID_SLOT) == INVALID_SLOT) {
            list.add("INVALID_SLOT");
            flipped |= INVALID_SLOT;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString(o & (~flipped)));
        }
        return String.join(" | ", list);
    }

};

