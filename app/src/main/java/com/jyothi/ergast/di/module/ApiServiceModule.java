package com.jyothi.ergast.di.module;

import com.jyothi.ergast.di.ErgastScope;
import com.jyothi.ergast.network.ApiService;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module(includes = NetworkModule.class)
public class ApiServiceModule {

    @Provides
    @ErgastScope
    public ApiService getErgastService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @ErgastScope
    public Retrofit getRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl("http://ergast.com/api/f1/")
                .build();
    }
}
