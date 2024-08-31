package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PacketInfo {

    @SerializedName("id")
    @Expose
    private int tagId;

    @SerializedName("rfid")
    @Expose
    private String rfid;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("partInfo")
    @Expose
    private QuantisedSparePartInfo partInfo;

    @SerializedName("location")
    @Expose
    private LocationInfo locationInfo;

    @SerializedName("type")
    @Expose
    private Type type;

    public enum Type {
        RECONDITIONED,
        NEW,
        OLD
    }

    public PacketInfo(PacketInfo packetInfo) {
        this.tagId = packetInfo.tagId;
        this.rfid = packetInfo.rfid;
        this.quantity = packetInfo.quantity;
        this.partInfo = packetInfo.partInfo;
        this.locationInfo = packetInfo.locationInfo;
        this.type = packetInfo.type;
    }

    public PacketInfo() {
    }

    public int getTagId() {
        return tagId;
    }

    public String getRfid() {
        return rfid;
    }

    public int getQuantity() {
        return quantity;
    }

    public QuantisedSparePartInfo getPartInfo() {
        return partInfo;
    }

    public LocationInfo getLocationInfo() {
        return locationInfo;
    }

    public Type getType() {
        return type;
    }

}
