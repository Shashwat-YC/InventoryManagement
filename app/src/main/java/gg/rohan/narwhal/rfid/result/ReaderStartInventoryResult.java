package gg.rohan.narwhal.rfid.result;

public enum ReaderStartInventoryResult {

    SUCCESS(0),
    OTHER_CMD_RUNNING_ERROR(-4),
    READER_OR_SERIAL_STATUS_ERROR(-7),
    SD_NOT_CONNECTED(-5);

    private final int result;

    ReaderStartInventoryResult(int result) {
        this.result = result;
    }

    public static ReaderStartInventoryResult fromInt(int result) {
        for (ReaderStartInventoryResult readerStartInventoryResult : values()) {
            if (readerStartInventoryResult.result == result) {
                return readerStartInventoryResult;
            }
        }
        return null;
    }

}
