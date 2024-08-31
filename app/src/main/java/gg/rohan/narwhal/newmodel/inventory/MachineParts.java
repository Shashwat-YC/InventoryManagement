package gg.rohan.narwhal.newmodel.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MachineParts {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rfid")
    @Expose
    private String rfid;
    @SerializedName("partInfo")
    @Expose
    private PartInfo partInfo;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("location")
    @Expose
    private Location location;

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

    public PartInfo getPartInfo() {
        return partInfo;
    }

    public void setPartInfo(PartInfo partInfo) {
        this.partInfo = partInfo;
    }
    public static class PartInfo {

        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("partNo")
        @Expose
        private String partNo;
        @SerializedName("machineModel")
        @Expose
        private String machineModel;

        @SerializedName("rob")
        @Expose
        private int rob;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPartNo() {
            return partNo;
        }

        public void setPartNo(String partNo) {
            this.partNo = partNo;
        }

        public String getMachineModel() {
            return machineModel;
        }

        public int getRob() {
            return rob;
        }

    }
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    public class Location {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("floor")
        @Expose
        private Integer floor;
        @SerializedName("floorName")
        @Expose
        private String floorName;
        @SerializedName("room")
        @Expose
        private Integer room;
        @SerializedName("roomName")
        @Expose
        private String roomName;
        @SerializedName("rack")
        @Expose
        private Integer rack;
        @SerializedName("shelf")
        @Expose
        private Integer shelf;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getFloor() {
            return floor;
        }

        public void setFloor(Integer floor) {
            this.floor = floor;
        }

        public String getFloorName() {
            return floorName;
        }

        public void setFloorName(String floorName) {
            this.floorName = floorName;
        }

        public Integer getRoom() {
            return room;
        }

        public void setRoom(Integer room) {
            this.room = room;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public Integer getRack() {
            return rack;
        }

        public void setRack(Integer rack) {
            this.rack = rack;
        }

        public Integer getShelf() {
            return shelf;
        }

        public void setShelf(Integer shelf) {
            this.shelf = shelf;
        }

    }
}