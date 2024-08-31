package gg.rohan.narwhal.newmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.temporal.TemporalAccessor;

public class JobInfo {

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("id")
    @Expose
    private int maintenanceRecordId;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("place")
    @Expose
    private Place place;

    @SerializedName("kind")
    @Expose
    private Kind kind;

    @SerializedName("scheduleInfo")
    @Expose
    private JobScheduleInfo scheduleInfo;

    @SerializedName("nextSchedule")
    @Expose
    private TemporalAccessor nextSchedule;

    @SerializedName("pic")
    @Expose
    private Pic pic;

    public JobInfo(JobInfo jobInfo) {
        this.code = jobInfo.code;
        this.description = jobInfo.description;
        this.place = jobInfo.place;
        this.kind = jobInfo.kind;
        this.scheduleInfo = jobInfo.scheduleInfo;
        this.nextSchedule = jobInfo.nextSchedule;
        this.pic = jobInfo.pic;
    }

    public JobInfo() {
    }

    public String getCode() {
        return code;
    }

    public int getMaintenanceRecordId() {
        return maintenanceRecordId;
    }

    public String getDescription() {
        return description;
    }

    public Place getPlace() {
        return place;
    }

    public Kind getKind() {
        return kind;
    }

    public JobScheduleInfo getScheduleInfo() {
        return scheduleInfo;
    }

    public TemporalAccessor getNextSchedule() {
        return nextSchedule;
    }

    public Pic getPic() {
        return pic;
    }

    public enum Place {
        @SerializedName("SAILING")
        SAILING,
        @SerializedName("DOCK")
        DOCK,
        @SerializedName("PORT")
        PORT
    }

    public enum Kind {
        PMS,
        PCM
    }

    public enum Pic {
        L1,
        L2,
        L3
    }
    
    public enum Status {
        COMPLETED,
        IN_PROGRESS,
        PLANNING
    }

    public enum DueWithin {
        DAILY,
        WEEKLY,
        MONTHLY,
        ALL
    }

}
