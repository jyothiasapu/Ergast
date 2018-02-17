package com.jyothi.ergast;

import android.app.Activity;
import android.app.Application;

import com.jyothi.ergast.di.ContextModule;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Ergast extends Application {

    private ErgastComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mComponent = DaggerErgastComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public ErgastComponent component() {
        return mComponent;
    }

    public static Ergast get(Activity act) {
        return (Ergast) act.getApplication();
    }

    public static Ergast get(Application app) {
        return (Ergast) app;
    }

}
