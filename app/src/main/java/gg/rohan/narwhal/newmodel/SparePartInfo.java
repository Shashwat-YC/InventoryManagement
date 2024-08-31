package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SparePartInfo {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("partNo")
    @Expose
    private String partNo;
    @SerializedName("machineInfo")
    @Expose
    private MachineModelInfo machineModel;

    @SerializedName("rob")
    @Expose
    private int rob;

    public SparePartInfo(SparePartInfo sparePartInfo) {
        this.code = sparePartInfo.code;
        this.name = sparePartInfo.name;
        this.partNo = sparePartInfo.partNo;
        this.machineModel = sparePartInfo.machineModel;
        this.rob = sparePartInfo.rob;
    }

    public SparePartInfo() {
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPartNo() {
        return partNo;
    }

    public MachineModelInfo getMachineModel() {
        return machineModel;
    }


}