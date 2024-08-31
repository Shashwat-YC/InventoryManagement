package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuantisedSparePartInfo extends SparePartInfo {

    @SerializedName("rob")
    @Expose
    private int rob;

    public QuantisedSparePartInfo(QuantisedSparePartInfo sparePartInfo) {
        super(sparePartInfo);
        this.rob = sparePartInfo.rob;
    }

    public QuantisedSparePartInfo() {
    }

    public int getRob() {
        return rob;
    }

}
