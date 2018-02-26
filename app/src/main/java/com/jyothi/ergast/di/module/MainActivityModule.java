package com.jyothi.ergast.di.module;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;

import com.jyothi.ergast.adapter.DriverAdapter;
import com.jyothi.ergast.data.DriverDataSourceFactory;
import com.jyothi.ergast.data.PageKeyedDriverSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.network.ApiService;
import com.jyothi.ergast.util.AppExecutors;
import com.jyothi.ergast.viewmodel.MainViewModel;
import com.jyothi.ergast.viewmodel.ViewModelFactory;

import java.util.concurrent.Executor;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module
public class MainActivityModule {

    @Provides
    ViewModelProvider.Factory getViewModelProvider(MainViewModel mainViewModel) {
        return new ViewModelFactory<>(mainViewModel);
    }

    @Provides
    MainViewModel getMainViewModel(Executor exe, DriverDataSourceFactory factory,
                                   DriversRepository repo) {
        return new MainViewModel(exe, factory, repo);
    }

    @Provides
    public DriverAdapter getItemAdapter() {
        return new DriverAdapter();
    }

    @Provides
    DriverDataSourceFactory getDriverDataSourceFactory(Executor netIO, ApiService apiService,
                                                       DriversRepository repo) {
        return new DriverDataSourceFactory(netIO, apiService, repo);
    }

    @Provides
    LiveData<PageKeyedDriverSource> getDriverSource(DriverDataSourceFactory factory) {
        return factory.getData();
    }

}
