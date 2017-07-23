package com.jyothi.ergast;

import android.app.Application;

import com.jyothi.ergast.network.NetworkQueue;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Ergast extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NetworkQueue.getInstance(this);
    }

}
