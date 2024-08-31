package gg.rohan.narwhal.rfid.message;

public class InventoryMessage {

    private final String epc;
    private final float rssi;

    InventoryMessage(String epc, float rssi) {
        this.epc = epc;
        this.rssi = rssi;
    }

    public static InventoryMessage fromString(String message) {
        String[] split = message.split(";");
        return new InventoryMessage(split[0], Float.parseFloat(split[1].substring(5)));
    }

    public String getEpc() {
        return epc;
    }

    public float getRssi() {
        return rssi;
    }

    @Override
    public String toString() {
        return "InventoryMessage{" +
                "epc='" + epc + '\'' +
                ", rssi=" + rssi +
                '}';
    }

}
