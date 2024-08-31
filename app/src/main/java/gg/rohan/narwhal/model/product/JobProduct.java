package gg.rohan.narwhal.model.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JobProduct {

    @SerializedName("rob")
    @Expose
    private int rob;
    @SerializedName("product")
    @Expose
    private Product product;
    @SerializedName("packets")
    @Expose
    private List<Packet> packets;
    @SerializedName("quantity_needed")
    @Expose
    private int quantityNeeded;

    private Integer checkOutQuantity = null;
    private boolean fulfilled = false;

    public int getRob() {
        return rob;
    }

    public void setRob(int rob) {
        this.rob = rob;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Packet> getPackets() {
        return packets;
    }

    public void setPackets(List<Packet> rfid) {
        this.packets = rfid;
    }

    public void setQuantityNeeded(int quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public int getCheckoutQuantity() {
        return this.checkOutQuantity != null ? checkOutQuantity : quantityNeeded;
    }

    public Integer getCheckOutQuantityNullable() {
        return this.checkOutQuantity;
    }

    public void setCheckOutQuantity(Integer checkOutQuantity) {
        this.checkOutQuantity = checkOutQuantity;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public void setFulfilled(boolean fulfilled) {
        this.fulfilled = fulfilled;
    }

    public static class Packet {
        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("rfid")
        @Expose
        private String rfid;
        @SerializedName("packet_type")
        @Expose
        private PacketType packetType;
        @SerializedName("quantity")
        @Expose
        private int quantity;
        @SerializedName("product_id")
        @Expose
        private String productId;
        @SerializedName("box_id")
        @Expose
        private int boxId;

        @SerializedName("floor")
        @Expose
        private int floor;

        @SerializedName("room")
        @Expose
        private String room;

        @SerializedName("rack")
        @Expose
        private int rack;

        @SerializedName("shelf")
        @Expose
        private int shelf;

        private boolean selected = false;
        private boolean scanned = false;

        private Integer updateQuantity;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRfid() {
            return rfid;
        }

        public void setRfid(String rfid) {
            this.rfid = rfid;
        }

        public PacketType getPacketType() {
            return packetType;
        }

        public void setPacketType(PacketType packetType) {
            this.packetType = packetType;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public int getBoxId() {
            return boxId;
        }

        public void setBoxId(int boxId) {
            this.boxId = boxId;
        }

        public int getFloor() {
            return floor;
        }

        public void setFloor(int floor) {
            this.floor = floor;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            this.room = room;
        }

        public int getRack() {
            return rack;
        }

        public void setRack(int rack) {
            this.rack = rack;
        }

        public int getShelf() {
            return shelf;
        }

        public void setShelf(int shelf) {
            this.shelf = shelf;
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

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isScanned() {
            return scanned;
        }

        public void setScanned(boolean scanned) {
            this.scanned = scanned;
        }

    }

}
