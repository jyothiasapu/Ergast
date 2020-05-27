package com.jyothi.ergast.data;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import androidx.annotation.NonNull;

import com.jyothi.ergast.data.source.DriversDataSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.model.DriverStub;
import com.jyothi.ergast.model.DriverTable;
import com.jyothi.ergast.model.ItemResponse;
import com.jyothi.ergast.model.MRData;
import com.jyothi.ergast.network.ApiService;
import com.jyothi.ergast.network.CallStatus;
import com.jyothi.ergast.network.LoadingState;
import com.jyothi.ergast.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jyothi on 25-01-2018.
 */

public class PageKeyedDriverSource extends PageKeyedDataSource<Integer, Driver> {

    private static final String TAG = "PageKeyedDriverSource";

    private LoadInitialParams<Integer> mInitialParams;
    private LoadParams<Integer> mAfterParams;

    private MutableLiveData<LoadingState> mNetworkState;
    private MutableLiveData<LoadingState> mInitialLoading;

    @Inject
    public ApiService mApiService;

    @Inject
    public DriversRepository mRepository;

    private Executor mExecutor;

    private int mPage = 0;
    private int mMaxPage;

    public PageKeyedDriverSource(Executor executor, ApiService api, DriversRepository repo) {
        mApiService = api;
        mNetworkState = new MutableLiveData<LoadingState>();
        mInitialLoading = new MutableLiveData<LoadingState>();
        mExecutor = executor;
        mRepository = repo;
    }

    public MutableLiveData<LoadingState> getNetworkState() {
        return mNetworkState;
    }

    public MutableLiveData<LoadingState> getInitialLoading() {
        return mInitialLoading;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Driver> callback) {
        mInitialParams = params;

        loadDrivers(callback, null);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Driver> callback) {
        // Nothing to do
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Driver> callback) {
        mAfterParams = params;

        loadDrivers(null, callback);
    }

    public void loadDrivers(final LoadInitialCallback<Integer, Driver> initialCallback,
                            final LoadCallback<Integer, Driver> callback) {
        mInitialLoading.postValue(LoadingState.LOADING);
        mNetworkState.postValue(LoadingState.LOADING);

        mRepository.getDrivers(mPage, new DriversDataSource.LoadDriversCallback() {

            @Override
            public void onDriversLoaded(List<Driver> drivers) {
                if (drivers == null || drivers.size() == 0) {
                    fetchDrivers(initialCallback, callback);
                    return;
                }

                mPage++;

                if (initialCallback != null) {
                    initialCallback.onResult(drivers, null, mPage);
                } else {
                    callback.onResult(drivers, mPage);
                }

                mInitialLoading.postValue(LoadingState.LOADED);
                mNetworkState.postValue(LoadingState.LOADED);
                mInitialParams = null;
            }

            @Override
            public void onDataNotAvailable() {
                fetchDrivers(initialCallback, callback);
            }
        });
    }

    private void fetchDrivers(final LoadInitialCallback<Integer, Driver> initialCallback,
                              final LoadCallback<Integer, Driver> callback) {
        mInitialLoading.postValue(LoadingState.LOADING);
        mNetworkState.postValue(LoadingState.LOADING);

        Call<ItemResponse> call = mApiService.getDrivers(mPage * Utils.ITEMS_PER_PAGE);
        call.enqueue(new Callback<ItemResponse>() {

            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                if (response == null) {
                    mInitialLoading.postValue(new LoadingState(CallStatus.FAILED, response.message()));
                    mNetworkState.postValue(new LoadingState(CallStatus.FAILED, response.message()));
                }

                if (!response.isSuccessful()) {
                    mInitialLoading.postValue(new LoadingState(CallStatus.FAILED, "Response Null"));
                    mNetworkState.postValue(new LoadingState(CallStatus.FAILED, "Response Null"));
                    return;
                }

                List<Driver> list = onFetchComplete(response.body(), (initialCallback != null));

                if (initialCallback != null) {
                    initialCallback.onResult(list, null, mPage);
                } else {
                    callback.onResult(list, mPage);
                }

                mInitialLoading.postValue(LoadingState.LOADED);
                mNetworkState.postValue(LoadingState.LOADED);
                mInitialParams = null;
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {
                String errorMessage;

                if (t == null) {
                    errorMessage = "error";
                } else {
                    errorMessage = t.getMessage();
                }

                mNetworkState.postValue(new LoadingState(CallStatus.FAILED, errorMessage));
            }
        });
    }

    public List<Driver> onFetchComplete(ItemResponse response, boolean isInitial) {
        List<Driver> list = new ArrayList<Driver>();

        MRData mrData = response.getMRData();
        if (mrData == null) {
            return list;
        }

        DriverTable dt = mrData.getDriverTable();
        if (dt == null) {
            return list;
        }

        int page = (Integer.parseInt(response.getMRData().getOffset()) / Utils.ITEMS_PER_PAGE);

        List<DriverStub> drivers = dt.getDrivers();
        if ((drivers != null) && (drivers.size() > 0)) {
            for (DriverStub ds : drivers) {
                Driver d = new Driver(ds.getDriverId(), ds.getUrl(), ds.getGivenName(),
                        ds.getFamilyName(), ds.getDateOfBirth(), ds.getNationality(), page);

                mRepository.saveDriver(d);
                list.add(d);
            }

            // Page maintenance
            mPage++;
        }

        if (isInitial) {
            int total = Integer.parseInt(response.getMRData().getTotal());
            mMaxPage = total / Utils.ITEMS_PER_PAGE;

            if (total > mMaxPage * Utils.ITEMS_PER_PAGE) {
                mMaxPage++;
            }
        }

        return list;
    }

    public void resetPage() {
        mPage = 0;
    }
}
