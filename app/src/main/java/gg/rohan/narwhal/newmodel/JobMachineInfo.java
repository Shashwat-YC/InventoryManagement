package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobMachineInfo {

    @SerializedName("job")
    @Expose
    private JobInfo jobInfo;

    @SerializedName("machine")
    @Expose
    private MachineInfo machineInfo;

    public JobInfo getJobInfo() {
        return jobInfo;
    }

    public MachineInfo getMachineInfo() {
        return machineInfo;
    }

}
