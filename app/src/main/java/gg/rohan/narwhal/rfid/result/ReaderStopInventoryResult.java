package gg.rohan.narwhal.rfid.result;

public enum ReaderStopInventoryResult {

    SUCCESS(0),
    OTHER_ERROR(-1),
    NOT_INVENTORY_STATE(-11),
    STOP_FAILED_TRY_AGAIN(-17);

    private final int result;

    ReaderStopInventoryResult(int result) {
        this.result = result;
    }

    public static ReaderStopInventoryResult fromInt(int result) {
        for (ReaderStopInventoryResult readerStopInventoryResult : values()) {
            if (readerStopInventoryResult.result == result) {
                return readerStopInventoryResult;
            }
        }
        return null;
    }

}
