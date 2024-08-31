package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.temporal.TemporalAccessor;

public class MachineInfo extends MachineModelInfo {

    @SerializedName("no")
    @Expose
    private int no;

    @SerializedName("lastMaintenance")
    @Expose
    private TemporalAccessor lastMaintenance;

    @SerializedName("avgRH")
    @Expose
    private int avgRH;

    public int getNo() {
        return no;
    }

    public TemporalAccessor getLastMaintenance() {
        return lastMaintenance;
    }

    public int getAvgRH() {
        return avgRH;
    }

}
