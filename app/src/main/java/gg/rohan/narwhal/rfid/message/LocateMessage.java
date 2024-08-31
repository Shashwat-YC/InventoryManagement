package gg.rohan.narwhal.rfid.message;

public class LocateMessage {

    private final float rssi;

    LocateMessage(float rssi) {
        this.rssi = rssi;
    }

    public static LocateMessage fromString(String message) {
        return new LocateMessage(Float.parseFloat(message));
    }

    public float getRssi() {
        return rssi;
    }

}
