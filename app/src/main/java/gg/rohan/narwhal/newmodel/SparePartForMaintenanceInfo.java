package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.skydoves.powermenu.annotations.Sp;

public class SparePartForMaintenanceInfo extends SparePartForJobInfo {

    @SerializedName("quantityNeededUpdated")
    @Expose
    private Integer quantityNeededUpdated;

    public SparePartForMaintenanceInfo(SparePartForJobInfo sparePartForJobInfo) {
        super(sparePartForJobInfo);
    }

    public SparePartForMaintenanceInfo(SparePartForMaintenanceInfo sparePartForMaintenanceInfo) {
        super(sparePartForMaintenanceInfo);
        this.quantityNeededUpdated = sparePartForMaintenanceInfo.quantityNeededUpdated;
    }

    public SparePartForMaintenanceInfo() {

    }

    public Integer getQuantityNeededUpdated() {
        return quantityNeededUpdated;
    }

    public Integer getQuantityNeededUpdatedOrElse(Integer defaultValue) {
        return quantityNeededUpdated != null ? quantityNeededUpdated : defaultValue;
    }

    public void setQuantityNeededUpdated(Integer quantityNeededUpdated) {
        this.quantityNeededUpdated = quantityNeededUpdated;
    }

}
