package gg.rohan.narwhal.rfid.result;

public enum ReaderStartLocatingResult {

    SUCCESS(0),
    OTHER_CMD_RUNNING_ERROR(-4),
    READER_OR_SERIAL_STATUS_ERROR(-7),
    SD_NOT_CONNECTED(-5);

    private final int value;

    ReaderStartLocatingResult(int value) {
        this.value = value;
    }

    public static ReaderStartLocatingResult fromInt(int value) {
        for (ReaderStartLocatingResult result : values()) {
            if (result.value == value) {
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    public int getValue() {
        return value;
    }

}
