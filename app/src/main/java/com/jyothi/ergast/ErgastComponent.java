package com.jyothi.ergast;

import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.data.source.local.ErgastDatabase;
import com.jyothi.ergast.di.ContextModule;
import com.jyothi.ergast.di.DatabaseModule;
import com.jyothi.ergast.di.ErgastScope;
import com.jyothi.ergast.di.ErgastServiceModule;
import com.jyothi.ergast.di.ExecutorModule;
import com.jyothi.ergast.di.NetworkModule;
import com.jyothi.ergast.network.ApiService;
import com.jyothi.ergast.util.AppExecutors;

import dagger.Component;

/**
 * Created by Jyothi on 16-02-2018.
 */

@ErgastScope
@Component(modules = {ContextModule.class, DatabaseModule.class, ExecutorModule.class,
        ErgastServiceModule.class, NetworkModule.class})
public interface ErgastComponent {

    AppExecutors getAppExecutors();

    DriversRepository getDriversRepository();

    ApiService getApiService();

    ErgastDatabase getDatabase();

}
