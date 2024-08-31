package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationInfo {

    @SerializedName("floor")
    @Expose
    private int floor;

    @SerializedName("floorName")
    @Expose
    private String floorName;

    @SerializedName("room")
    @Expose
    private int room;

    @SerializedName("roomName")
    @Expose
    private String roomName;

    @SerializedName("rack")
    @Expose
    private Integer rack;

    @SerializedName("shelf")
    @Expose
    private Integer shelf;

    @SerializedName("id")
    @Expose
    private int id;

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public String getFloorName() {
        return floorName;
    }

    public int getRoom() {
        return room;
    }

    public String getRoomName() {
        return roomName;
    }

    public Integer getRack() {
        return rack;
    }

    private boolean hasRack() {
        return rack != null;
    }

    public Integer getShelf() {
        return shelf;
    }

    private boolean hasShelf() {
        return shelf != null;
    }

    private boolean isShelved() {
        return hasRack() && hasShelf();
    }

}
