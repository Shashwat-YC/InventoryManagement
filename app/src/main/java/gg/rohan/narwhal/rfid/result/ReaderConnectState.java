package gg.rohan.narwhal.rfid.result;

public enum ReaderConnectState {

    DISCONNECTED(0),
    CONNECTED(1),
    OTHER_CMD_RUNNING_ERROR(-4),
    READER_OR_SERIAL_STATUS_ERROR(-7);

    private final int value;

    ReaderConnectState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ReaderConnectState fromInt(int value) {
        for (ReaderConnectState state : ReaderConnectState.values()) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return null;
    }
}
