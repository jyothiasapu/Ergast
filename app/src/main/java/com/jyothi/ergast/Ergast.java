package com.jyothi.ergast;

import android.app.Activity;
import android.app.Application;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Ergast extends Application implements HasActivityInjector {

    @Inject
    public DispatchingAndroidInjector<Activity> mActivityInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        DaggerErgastComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    public static Ergast get(Activity act) {
        return (Ergast) act.getApplication();
    }

    public static Ergast get(Application app) {
        return (Ergast) app;
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mActivityInjector;
    }

}
