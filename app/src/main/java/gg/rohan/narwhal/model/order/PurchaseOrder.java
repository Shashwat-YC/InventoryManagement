package gg.rohan.narwhal.model.order;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PurchaseOrder {

    @SerializedName("po_no")
    @Expose
    private String purchaseOrderNo;

    @SerializedName("po_status")
    @Expose
    private PurchaseOrderStatus purchaseOrderStatus;

    @SerializedName("po_supplier")
    @Expose
    private String supplier;

    public PurchaseOrder(String purchaseOrderNo, PurchaseOrderStatus purchaseOrderStatus, String supplier) {
        this.purchaseOrderNo = purchaseOrderNo;
        this.purchaseOrderStatus = purchaseOrderStatus;
        this.supplier = supplier;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public PurchaseOrderStatus getPurchaseOrderStatus() {
        return purchaseOrderStatus;
    }

    public void setPurchaseOrderStatus(PurchaseOrderStatus purchaseOrderStatus) {
        this.purchaseOrderStatus = purchaseOrderStatus;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

}
