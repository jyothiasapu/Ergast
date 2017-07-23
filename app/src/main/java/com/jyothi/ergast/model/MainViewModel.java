package com.jyothi.ergast.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.jyothi.ergast.R;
import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.data.source.DriversDataSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.interfaces.MainViewInterface;
import com.jyothi.ergast.interfaces.NetworkCallback;
import com.jyothi.ergast.network.NetworkQueue;
import com.jyothi.ergast.network.RequestFetcher;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class MainViewModel extends AndroidViewModel implements NetworkCallback, MainViewInterface {

    private static final String TAG = "MainViewModel";

    private volatile int mPage = 0;

    private DriversRepository mRepository;
    private MutableLiveData<List<Driver>> mDrivers;
    private MutableLiveData<Boolean> mQueryDone;
    private MutableLiveData<Boolean> mShowProgress;
    private RequestFetcher mRequestFetcher;
    private NetworkQueue mNetworkQueue;

    public MainViewModel(Application app) {
        super(app);

        mPage = Utils.readPagePref(app.getApplicationContext(), mPage);

        mRepository = ActivityUtils.provideTasksRepository(app.getApplicationContext());
        checkNotNull(mRepository, "tasksRepository cannot be null");

        mRequestFetcher = new RequestFetcher(this);
        mNetworkQueue = NetworkQueue.getInstance(app.getApplicationContext());

        mQueryDone = new MutableLiveData<Boolean>();
        mQueryDone.setValue(true);

        mShowProgress = new MutableLiveData<Boolean>();
        mShowProgress.setValue(true);

        getDrivers();
    }

    private void loadUsers() {
        mQueryDone.setValue(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringRequest req = mRequestFetcher.getRequest(mPage);
                mNetworkQueue.addToRequestQueue(req);
            }
        }).start();
    }

    public void loadUsers(boolean forceUpdate, final boolean showLoadingUI) {
        mQueryDone.setValue(false);
        if (showLoadingUI) {
            mShowProgress.setValue(true);
        }

        if (forceUpdate) {
            mRepository.refreshDrivers();
        }

        mRepository.getDrivers(new DriversDataSource.LoadDriversCallback() {
            @Override
            public void onDriversLoaded(List<Driver> drivers) {
                if (drivers == null || drivers.size() == 0) {
                    loadUsers();
                }

                List<Driver> list = mDrivers.getValue();
                if (list == null) {
                    list = new ArrayList<Driver>();
                }

                for (Driver d : drivers) {
                    list.add(d);
                }

                mDrivers.setValue(list);

                if (showLoadingUI) {
                    mShowProgress.setValue(false);
                }

                mQueryDone.setValue(true);
            }

            @Override
            public void onDataNotAvailable() {
                loadUsers();
            }
        });
    }

    @Override
    public LiveData<List<Driver>> getDrivers() {
        if (mDrivers == null) {
            mDrivers = new MutableLiveData<List<Driver>>();
            loadUsers(false, true);
        }

        return mDrivers;
    }

    @Override
    public LiveData<Boolean> getShowProgress() {
        return mShowProgress;
    }

    @Override
    public LiveData<Boolean> getQueryDone() {
        return mQueryDone;
    }

    @Override
    public void refresh() {
        mPage = 0;
        Utils.writePagePref(getApplication().getApplicationContext(), 0);
        mRepository.refreshDrivers();
    }

    @Override
    public void getDriverWithDriverId(String driverId) {
        mRepository.getDriver(driverId + "%", new DriversDataSource.GetDriverCallback() {

            @Override
            public void onDriverLoaded(List<Driver> list) {
                mDrivers.setValue(list);
            }

            @Override
            public void onDataNotAvailable() {
                List<Driver> list = mDrivers.getValue();
                list.clear();
                mDrivers.setValue(list);

                Toast.makeText(getApplication().getApplicationContext(),
                        R.string.driver_not_present, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loadNextSetUsers() {
        mQueryDone.setValue(false);
        mShowProgress.setValue(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                StringRequest req = mRequestFetcher.getRequest(mPage);
                mNetworkQueue.addToRequestQueue(req);
            }
        }).start();
    }

    @Override
    public void doOnQueryDone(ItemResponse response) {
        // Page maintenance
        mPage++;
        Utils.writePagePref(getApplication().getApplicationContext(), mPage);

        List<Driver> list = mDrivers.getValue();
        if (list == null) {
            list = new ArrayList<Driver>();
        }

        List<DriverStub> drivers = response.getMRData().getDriverTable().getDrivers();
        for (DriverStub ds : drivers) {
            Driver d = new Driver(ds.getDriverId(), ds.getUrl(), ds.getGivenName(),
                    ds.getFamilyName(), ds.getDateOfBirth(), ds.getNationality());

            mRepository.saveDriver(d);
            list.add(d);
        }

        mDrivers.setValue(list);
        mQueryDone.setValue(true);
        mShowProgress.setValue(false);
    }

    @Override
    public void doOnError() {

    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (mRequestFetcher != null) {
            mRequestFetcher.tearDown();
            mRequestFetcher = null;
        }

        if (mDrivers != null) {
            mDrivers.setValue(null);
            mDrivers = null;
        }

        mNetworkQueue = null;

        mRepository.destroyInstance();
    }

    public void clearDrivers() {
        List<Driver> list = mDrivers.getValue();
        list.clear();
        mDrivers.setValue(list);
    }
}
