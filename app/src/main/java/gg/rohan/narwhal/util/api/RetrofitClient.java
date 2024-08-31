package gg.rohan.narwhal.util.api;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.temporal.TemporalAccessor;
import java.util.concurrent.TimeUnit;

import gg.rohan.narwhal.util.Storage;
import gg.rohan.narwhal.util.gson.TemporalSerializer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;
    private static final long CONNECTION_TIMEOUT = 120;
    private static final String RETROFIT_LOGGER = "Result";

    public static void setup(Context context) {
        if (instance != null) throw new IllegalStateException("Already initialised");
        instance = buildRetrofit(context);
    }

    private static Retrofit buildRetrofit(Context context) {
        OkHttpClient client = getOkHttpClient();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TemporalAccessor.class, new TemporalSerializer())
                .create();
        return new Retrofit.Builder()
                .baseUrl("http://" + Storage.getString(context, "ip_address", "159.89.204.17") + "/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static Pms getPmsAdapter() {
        return instance.create(Pms.class);
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Log.e(RETROFIT_LOGGER, message));

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okClientBuilder.addInterceptor(httpLoggingInterceptor);
        okClientBuilder.addNetworkInterceptor(new StethoInterceptor());
        okClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        okClientBuilder.readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        okClientBuilder.writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        return okClientBuilder.build();
    }
}
