package gg.rohan.narwhal.rfid.result;

public enum ReaderDisconnectResult {

    SUCCESS(0),
    READER_OR_SERIAL_STATUS_ERROR(-7),
    DUP_CMD_ERROR(-8),
    ALREADY_DISCONNECTED(-9),
    ACCESS_TIMEOUT(-32);

    private final int result;

    ReaderDisconnectResult(int result) {
        this.result = result;
    }

    public static ReaderDisconnectResult fromInt(int result) {
        for (ReaderDisconnectResult readerDisconnectResult : values()) {
            if (readerDisconnectResult.result == result) {
                return readerDisconnectResult;
            }
        }
        return null;
    }

}
