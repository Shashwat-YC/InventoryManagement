package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobScheduleInfo {

    @SerializedName("type")
    @Expose
    private Type type;

    @SerializedName("delay")
    @Expose
    private int delay;

    enum Type {
        CA,
        RH
    }

}
