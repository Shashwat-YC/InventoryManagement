package gg.rohan.narwhal.util.newapi;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

import gg.rohan.narwhal.newmodel.PacketInfo;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PacketService {

    @GET(NewApiUrls.SINGLE_PACKET_INFO)
    Observable<PacketInfo> getPacketInfo(@Path("rfid") String rfid);

    @POST(NewApiUrls.MULTI_PACKET_INFO)
    Observable<@Unmodifiable List<PacketInfo>> getPacketInfo(@Body List<String> rfidTagList);

    @POST(NewApiUrls.UPDATE_PACKET)
    @ApiStatus.Internal
    Observable<Void> updatePacketQuantity(@Body JsonObject jsonObject);

    default Observable<Void> updatePacketQuantityFromId(int tagId, int quantity) {
        JsonObject updatePacketQuantity = new JsonObject();
        updatePacketQuantity.addProperty("id", tagId);
        updatePacketQuantity.addProperty("quantity", quantity);
        return updatePacketQuantity(updatePacketQuantity);
    }

    @POST(NewApiUrls.UPDATE_PACKET_FOR_IN_USE)
    Observable<Void> updatePacketForInUseQuantityFromId(@Path("tagId") int tagId, @Query("quantity") int quantity);

}
