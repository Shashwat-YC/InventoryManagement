package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SparePartWithPacketInfo extends QuantisedSparePartInfo {

    @SerializedName("packets")
    @Expose
    private List<PacketInfo> packets;

    public SparePartWithPacketInfo(QuantisedSparePartInfo sparePartInfo) {
        super(sparePartInfo);
    }

    public List<PacketInfo> getPackets() {
        return packets;
    }

}
