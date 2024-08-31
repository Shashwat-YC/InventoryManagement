package gg.rohan.narwhal.model.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PacketsFromBox {
    @SerializedName("rob")
    @Expose
    private Integer rob;
    @SerializedName("product")
    @Expose
    private Product product;
    @SerializedName("packets")
    @Expose
    private List<Packet> packets;

    public Integer getRob() {
        return rob;
    }

    public void setRob(Integer rob) {
        this.rob = rob;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    public static class Product {

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
    public List<Packet> getPackets() {
        return packets;
    }

    public void setPackets(List<Packet> packets) {
        this.packets = packets;
    }
    public static class Packet {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("rfid")
        @Expose
        private String rfid;
        @SerializedName("packet_type")
        @Expose
        private String packetType;
        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("box_id")
        @Expose
        private Integer boxId;

        @SerializedName("material_desc")
        @Expose
        private String materialDesc;

        private Integer updateQuantity;
        private boolean found;


        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getRfid() {
            return rfid;
        }

        public void setRfid(String rfid) {
            this.rfid = rfid;
        }

        public String getPacketType() {
            return packetType;
        }

        public void setPacketType(String packetType) {
            this.packetType = packetType;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getBoxId() {
            return boxId;
        }

        public void setBoxId(Integer boxId) {
            this.boxId = boxId;
        }

        public String getMaterialDesc() {
            return materialDesc;
        }

        public void setMaterialDesc(String materialDesc) {
            this.materialDesc = materialDesc;
        }

        public int getUpdateQuantity() {
            return this.updateQuantity != null ? this.updateQuantity : this.quantity;
        }

        public Integer getUpdateQuantityNullable() {
            return this.updateQuantity;
        }

        public void setUpdateQuantity(Integer updateQuantity) {
            this.updateQuantity = updateQuantity;
        }

        public boolean isFound() {
            return found;
        }

        public void setFound(boolean found) {
            this.found = found;
        }

    }
}
