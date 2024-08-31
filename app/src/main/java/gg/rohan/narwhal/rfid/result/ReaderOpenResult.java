package gg.rohan.narwhal.rfid.result;

public enum ReaderOpenResult {

    SUCCESS,
    FAIL;

    public static ReaderOpenResult fromBoolean(boolean result) {
        return result ? SUCCESS : FAIL;
    }

}
