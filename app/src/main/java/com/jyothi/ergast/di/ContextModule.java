package com.jyothi.ergast.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module
public class ContextModule {

    private final Context ctx;

    public ContextModule(Context ctx) {
        this.ctx = ctx;
    }

    @Provides
    @ErgastScope
    @ApplicationContext
    public Context getApplicationContext() {
        return ctx;
    }

}
