package gg.rohan.narwhal.util.newapi;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import gg.rohan.narwhal.newmodel.MachineInfo;
import gg.rohan.narwhal.newmodel.SparePartInfo;
import gg.rohan.narwhal.newmodel.inventory.MachineBox;
import gg.rohan.narwhal.newmodel.inventory.MachineParts;
import gg.rohan.narwhal.newmodel.InventoryRoom;
import gg.rohan.narwhal.newmodel.SparePartWithPacketInfo;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InventoryService {

    @GET(NewApiUrls.INVENTORY_SPARE_PART)
    Observable<SparePartWithPacketInfo> getSparePartWithPackets(@Path("spare_part_code") String sparePartCode);

    @GET(NewApiUrls.INVENTORY_SPARE_PART_FOR_MAINTENANCE)
    Observable<SparePartWithPacketInfo> getMaintenanceSparePartWithPackets(@Path("maintenanceId") int maintenanceId, @Query("sparePartCode") String sparePartCode);

    @GET(NewApiUrls.INVENTORY_FETCH_MACHINES)
    Call<List<MachineInfo>> getMachines(@Path("floor") int floor);

    @GET(NewApiUrls.INVENTORY_MACHINE_LOCATIONS)
    Call<List<MachineBox>> getMachineLocations(@Path("model") String model);

    @POST(NewApiUrls.INVENTORY_LINK_NEW_LOCATIONS_MACHINE)
    Call<Integer> addLinkedLocation(@Body JsonObject requestBody);

    default Call<Integer> addLinkedLocation(String model, int room, Integer rack, Integer shelf) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("roomId", room);
        if (rack != null) requestBody.addProperty("rack", rack);
        if (shelf != null) requestBody.addProperty("shelf", shelf);
        return addLinkedLocation(requestBody);
    }

    @POST(NewApiUrls.INVENTORY_UPDATE_LOCATION_MACHINE)
    Call<Void> updateLinkedLocation(@Body JsonObject requestBody);

    default Call<Void> updateLinkedLocation(String model, int oldLocationId, int room, Integer rack, Integer shelf) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("oldLocationId", oldLocationId);
        requestBody.addProperty("roomId", room);
        if (rack != null) requestBody.addProperty("rack", rack);
        if (shelf != null) requestBody.addProperty("shelf", shelf);
        return updateLinkedLocation(requestBody);
    }

    @GET(NewApiUrls.INVENTORY_FETCH_ROOMS)
    Call<List<InventoryRoom>> getRooms(@Path("floor") int floor);

    @GET(NewApiUrls.INVENTORY_FETCH_LOCATION_PACKETS_BY_LOCATION)
    Call<List<MachineParts>> getLocationPacketsByLocation(@Path("location") String location, @Path("machine") String machine);

    @POST(NewApiUrls.ASSIGN_PACKET)
    Call<Void> assignPacket(@Body Map<String, Object> requestBody);

    @POST(NewApiUrls.INVENTORY_UPDATE_PACKET)
    Call<Void> updatePacket(@Body Map<String, Object> requestBody);

    @GET(NewApiUrls.INVENTORY_FETCH_MACHINE_PARTS)
    Call<List<SparePartInfo>> getMachineParts(@Path("machine") String machine);

    @GET(NewApiUrls.INVENTORY_FETCH_MACHINE_INFO)
    Observable<JsonObject> getMachineInfo(@Path("model") String machine);

    @POST(NewApiUrls.TEST_CONNECTION)
    Call<Void> testConnection();

}
