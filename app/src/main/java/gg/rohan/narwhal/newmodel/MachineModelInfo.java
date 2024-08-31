package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MachineModelInfo {

    @SerializedName("model")
    @Expose
    private String model;

    @SerializedName("maker")
    @Expose
    private String maker;

    @SerializedName("name")
    @Expose
    private String name;

    public MachineModelInfo(MachineModelInfo machineModelInfo) {
        this.model = machineModelInfo.model;
        this.maker = machineModelInfo.maker;
        this.name = machineModelInfo.name;
    }

    public MachineModelInfo() {
    }

    public String getModel() {
        return model;
    }

    public String getMaker() {
        return maker;
    }

    public String getName() {
        return name;
    }

}
