package com.jyothi.ergast.network;


import com.jyothi.ergast.model.ItemResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    // Gets list of environments
    @GET("drivers.json?limit=10&")
    Call<ItemResponse> getDrivers(@Query("offset") Integer offset);

}
