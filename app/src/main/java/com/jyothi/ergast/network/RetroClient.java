package com.jyothi.ergast.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetroClient {

    private static final String ROOT_URL = "http://ergast.com/api/f1/";

    private static Retrofit INSTANCE;

    private static final Object sLock = new Object();

    private RetroClient() {

    }

    private static Retrofit getRetrofitInstance() {
        if (INSTANCE == null) {
            synchronized (sLock) {
                if (INSTANCE == null) {
                    INSTANCE = new Retrofit.Builder()
                            .baseUrl(ROOT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * Get API Service
     *
     * @return API Service
     */
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}