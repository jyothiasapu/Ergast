package com.jyothi.ergast.viewmodel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.appcompat.app.AppCompatActivity;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.data.DriverDataSourceFactory;
import com.jyothi.ergast.data.PageKeyedDriverSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.interfaces.MainViewInterface;
import com.jyothi.ergast.network.LoadingState;

import java.util.concurrent.Executor;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class MainViewModel extends ViewModel implements MainViewInterface {

    private static final String TAG = "MainViewModel";

    private LiveData<PagedList<Driver>> mDrivers = null;
    private LiveData<PageKeyedDriverSource> mDataSource;
    private LiveData<LoadingState> mLoadingState;

    public Executor mExecutors;

    public DriversRepository mRepository;

    public DriverDataSourceFactory mFactory;

    public MainViewModel(Executor executors, DriverDataSourceFactory factory,
                         DriversRepository repo) {
        mExecutors = executors;
        mFactory = factory;
        mRepository = repo;

        mDrivers = createDriversSource();
    }

    private LiveData<PagedList<Driver>> createDriversSource() {
        mDataSource = mFactory.getData();

        mLoadingState = Transformations.switchMap(mDataSource,
                new Function<PageKeyedDriverSource, LiveData<LoadingState>>() {
                    @Override
                    public LiveData<LoadingState> apply(PageKeyedDriverSource input) {
                        return mDataSource.getValue().getNetworkState();
                    }
                });

        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder()).setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(2)
                        .setPageSize(4).build();

        return (new LivePagedListBuilder(mFactory, pagedListConfig))
                .setFetchExecutor(mExecutors)
                .build();
    }

    @Override
    public LiveData<PagedList<Driver>> getDrivers() {
        return mDrivers;
    }

    @Override
    public void removeDriversObserver(AppCompatActivity act) {
        if (mDrivers == null) {
            return;
        }

        mDrivers.removeObservers(act);
    }

    @Override
    public void refresh() {
        mRepository.deleteAllDrivers();
        mDataSource.getValue().resetPage();
        mDrivers = createDriversSource();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        mRepository = null;
        mExecutors = null;
    }

}
