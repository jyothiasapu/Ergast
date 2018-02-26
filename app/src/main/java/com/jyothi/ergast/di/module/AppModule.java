package com.jyothi.ergast.di.module;

import dagger.Module;

/**
 * Created by jasapu on 26-02-2018.
 */

@Module(includes = {ApiServiceModule.class, NetworkModule.class, ContextModule.class,
        DatabaseModule.class, ExecutorModule.class, NetworkModule.class})
public class AppModule {
}
