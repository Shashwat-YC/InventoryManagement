package gg.rohan.narwhal.model.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("material_desc")
    @Expose
    private String materialDesc;
    @SerializedName("maker_desc")
    @Expose
    private String makerDesc;
    @SerializedName("part_no")
    @Expose
    private String partNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMakerDesc() {
        return makerDesc;
    }

    public void setMakerDesc(String makerDesc) {
        this.makerDesc = makerDesc;
    }

    public String getPartNo() {
        return partNo;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }
}
