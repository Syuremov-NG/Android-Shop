package com.shopm2.shop.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShopService {
    ShopApi api;

    private static ShopService mInstance;
    private static final String BASE_URL = "http://192.168.125.8:80/rest/all/";
    private final Retrofit mRetrofit;

    public ShopService() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ShopService getInstance() {
        if (mInstance == null) {
            mInstance = new ShopService();
        }
        return mInstance;
    }

    public ShopApi getShopApi() {
        return mRetrofit.create(ShopApi.class);
    }
}
