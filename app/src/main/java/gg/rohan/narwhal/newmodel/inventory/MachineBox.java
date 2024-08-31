package gg.rohan.narwhal.newmodel.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MachineBox {

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