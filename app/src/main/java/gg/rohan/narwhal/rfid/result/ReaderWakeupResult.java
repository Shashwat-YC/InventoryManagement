package gg.rohan.narwhal.rfid.result;

public enum ReaderWakeupResult {

    SLEEP,
    WAKEUP;

    public static ReaderWakeupResult fromInt(int result) {
        return result == 0 ? SLEEP : WAKEUP;
    }

}
