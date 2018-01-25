package com.jyothi.ergast;

import android.app.Application;

import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.AppExecutors;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Ergast extends Application {

    private AppExecutors mExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mExecutors = new AppExecutors();

        ActivityUtils.provideErgastRepository(this, mExecutors);
    }

    public AppExecutors getExecutors() {
        return mExecutors;
    }

}
