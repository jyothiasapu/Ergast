package com.jyothi.ergast.di.module;

import android.content.Context;
import android.util.Log;

import com.jyothi.ergast.di.ApplicationContext;
import com.jyothi.ergast.di.ErgastScope;
import com.jyothi.ergast.di.module.ContextModule;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Jyothi Asapu on 16-02-2018.
 */

@Module(includes = {ContextModule.class})
public class NetworkModule {

    private static final String TAG = "NetworkModule";

    @Provides
    @ErgastScope
    public HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    @Provides
    @ErgastScope
    public Cache getCache(File cacheFile) {
        return new Cache(cacheFile, 10 * 1000 * 1000); //10MB Cache
    }

    @Provides
    @ErgastScope
    public File getCacheFile(@ApplicationContext Context context) {
        return new File(context.getCacheDir(), "okhttp_cache");
    }

    @Provides
    @ErgastScope
    public OkHttpClient getOkHttpClient(HttpLoggingInterceptor loggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .build();
    }
}
