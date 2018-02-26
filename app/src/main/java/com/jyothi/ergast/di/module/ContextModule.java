package com.jyothi.ergast.di.module;

import android.app.Application;
import android.content.Context;

import com.jyothi.ergast.di.ApplicationContext;
import com.jyothi.ergast.di.ErgastScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi Asapu on 16-02-2018.
 */

@Module
public class ContextModule {

    @Provides
    @ErgastScope
    @ApplicationContext
    public Context getApplicationContext(Application app ) {
        return app;
    }

}
