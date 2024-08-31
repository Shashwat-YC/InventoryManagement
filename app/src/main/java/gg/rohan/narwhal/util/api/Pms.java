package gg.rohan.narwhal.util.api;

import java.util.List;

import gg.rohan.narwhal.model.product.JobProduct;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Pms {

    @GET(ApiUrls.PMS_PRODUCTS)
    Call<List<JobProduct>> getProducts(@Query("job_id") String jobId);


}
