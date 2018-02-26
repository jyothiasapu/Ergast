package com.jyothi.ergast.di.module;

import com.jyothi.ergast.di.ErgastScope;
import com.jyothi.ergast.util.AppExecutors;
import com.jyothi.ergast.util.DiskIOThreadExecutor;

import java.util.concurrent.Executor;

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

    @Provides
    @ErgastScope
    public DiskIOThreadExecutor getDiskIO(AppExecutors exe) {
        return exe.diskIO();
    }

    @Provides
    @ErgastScope
    public AppExecutors.MainThreadExecutor getMainThread(AppExecutors exe) {
        return exe.mainThread();
    }

    @Provides
    @ErgastScope
    public Executor getNetworkIO(AppExecutors exe) {
        return exe.networkIO();
    }
}
