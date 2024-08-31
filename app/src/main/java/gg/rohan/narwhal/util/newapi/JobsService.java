package gg.rohan.narwhal.util.newapi;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

import gg.rohan.narwhal.newmodel.JobInfo;
import gg.rohan.narwhal.newmodel.JobMachineInfo;
import gg.rohan.narwhal.newmodel.SparePartForJobInfo;
import gg.rohan.narwhal.newmodel.SparePartForMaintenanceInfo;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JobsService {

    @GET(NewApiUrls.JOBS)
    @ApiStatus.Internal
    Observable<@Unmodifiable List<JobMachineInfo>> getJobsInternal(
            @Query("status") JobInfo.Status jobStatus,
            @Query("dueWithin") JobInfo.DueWithin dueWithin,
            @Query("place") JobInfo.Place place,
            @Query("limit") int limit
    );

    default Observable<@Unmodifiable List<JobMachineInfo>> getJobs(
            @Query("status") JobInfo.Status jobStatus,
            @Query("dueWithin") JobInfo.DueWithin dueWithin,
            @Query("place") JobInfo.Place place,
            @Query("limit") int limit
    ) {
        return getJobsInternal(jobStatus, dueWithin  == JobInfo.DueWithin.ALL ? null : dueWithin, place, limit);
    }

    default Observable<@Unmodifiable List<JobMachineInfo>> getAllJobs(
            @Query("status") JobInfo.Status jobStatus,
            @Query("dueWithin") JobInfo.DueWithin dueWithin,
            @Query("place") JobInfo.Place place
    ) {
        return getJobs(jobStatus, dueWithin, place, -1);
    }

    @POST(NewApiUrls.JOB_START)
    Observable<Void> startJob(@Path("code") Integer maintenanceRecordId, @Body List<Integer> usedPackets);

    @POST(NewApiUrls.JOB_COMPLETE)
    Observable<Void> completeJob(@Path("code") Integer maintenanceRecordId, @Body Map<Integer, Integer> usedQuantities);

    @GET(NewApiUrls.JOB_SPARE_PARTS)
    Observable<List<SparePartForJobInfo>> getJobSpareParts(@Path("code") String pmsCode);

    @GET(NewApiUrls.JOB_SPARE_PARTS_FOR_MAINTENANCE)
    Observable<List<SparePartForMaintenanceInfo>> getJobSparePartsForMaintenance(@Path("maintenanceId") int maintenanceId);

    @POST(NewApiUrls.UPDATE_SPARE_PART_MAINTENANCE_QUANTITY)
    @ApiStatus.Internal
    Observable<Void> updateSparePartMaintenanceQuantity(@Body JsonObject body);

    default Observable<Void> updateSparePartMaintenanceQuantity(int maintenanceId, String sparePartCode, int quantity) {
        JsonObject body = new JsonObject();
        body.addProperty("maintenanceId", maintenanceId);
        body.addProperty("sparePartCode", sparePartCode);
        body.addProperty("quantity", quantity);
        return updateSparePartMaintenanceQuantity(body);
    }



}
