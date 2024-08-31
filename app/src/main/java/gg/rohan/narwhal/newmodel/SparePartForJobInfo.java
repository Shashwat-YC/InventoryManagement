package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SparePartForJobInfo extends QuantisedSparePartInfo {

    @SerializedName("quantityNeeded")
    @Expose
    private int quantityNeeded;

    public SparePartForJobInfo(SparePartForJobInfo sparePartForJobInfo) {
        super(sparePartForJobInfo);
        this.quantityNeeded = sparePartForJobInfo.quantityNeeded;
    }

    public SparePartForJobInfo(QuantisedSparePartInfo quantisedSparePartInfo) {
        super(quantisedSparePartInfo);
    }

    public SparePartForJobInfo() {

    }

    public int getQuantityNeeded() {
        return quantityNeeded;
    }

}
