package gg.rohan.narwhal.rfid.result;

public enum ReaderConnectResult {

    SUCCESS(0),
    OTHER_CMD_RUNNING_ERROR(-4),
    READER_OR_SERIAL_STATUS_ERROR(-7),
    DUP_CMD_ERROR(-8),
    ALREADY_CONNECTED(-10),
    ACCESS_TIMEOUT(-32);

    private final int result;

    ReaderConnectResult(int result) {
        this.result = result;
    }

    public static ReaderConnectResult fromInt(int result) {
        for (ReaderConnectResult readerConnectResult : values()) {
            if (readerConnectResult.result == result) {
                return readerConnectResult;
            }
        }
        return null;
    }

}
