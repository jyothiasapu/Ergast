package com.jyothi.ergast.di;

import com.jyothi.ergast.util.AppExecutors;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module
public class ExecutorModule {

    @Provides
    @ErgastScope
    public AppExecutors getExecutors() {
        return new AppExecutors();
    }
}
