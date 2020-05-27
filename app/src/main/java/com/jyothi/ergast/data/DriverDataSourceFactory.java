package com.jyothi.ergast.data;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.network.ApiService;

import java.util.concurrent.Executor;

/**
 * Created by Jyothi on 23-02-2018.
 */

public class DriverDataSourceFactory extends DataSource.Factory {

    private MutableLiveData<PageKeyedDriverSource> mData;
    private PageKeyedDriverSource mDataSource;
    private ApiService mApiService;
    private DriversRepository mRepo;
    private Executor mExecutor;

    public DriverDataSourceFactory(Executor executor, ApiService apiService,
                                   DriversRepository repo) {
        mExecutor = executor;
        mApiService = apiService;
        mRepo = repo;
        mData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        mDataSource = new PageKeyedDriverSource(mExecutor, mApiService, mRepo);
        mData.postValue(mDataSource);
        return mDataSource;
    }

    public MutableLiveData<PageKeyedDriverSource> getData() {
        return mData;
    }

}
