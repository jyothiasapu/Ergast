package com.jyothi.ergast.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.toolbox.JsonRequest;
import com.jyothi.ergast.Ergast;
import com.jyothi.ergast.R;
import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.data.source.DriversDataSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.interfaces.MainViewInterface;
import com.jyothi.ergast.interfaces.NetworkCallback;
import com.jyothi.ergast.network.NetworkQueue;
import com.jyothi.ergast.network.RequestFetcher;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.AppExecutors;
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
    private volatile MutableLiveData<Boolean> mEndOfDrivers;

    private AppExecutors mExecutors;
    private DriversRepository mRepository;
    private MutableLiveData<List<Driver>> mDrivers;
    private MutableLiveData<Boolean> mQueryDone;
    private MutableLiveData<Boolean> mShowProgress;
    private RequestFetcher mRequestFetcher;
    private NetworkQueue mNetworkQueue;

    public MainViewModel(Application app) {
        super(app);

        mPage = Utils.readPagePref(app.getApplicationContext(), mPage);
        mEndOfDrivers = new MutableLiveData<Boolean>();
        mEndOfDrivers.setValue(Utils.readEndOfDriversPref(app.getApplicationContext()));

        mExecutors = ((Ergast) app).getExecutors();
        mRepository = ActivityUtils.provideTasksRepository(app.getApplicationContext(),
                mExecutors);
        checkNotNull(mRepository, "tasksRepository cannot be null");

        mRequestFetcher = new RequestFetcher(this, mExecutors);
        mNetworkQueue = NetworkQueue.getInstance(app.getApplicationContext());

        mQueryDone = new MutableLiveData<Boolean>();
        mQueryDone.setValue(true);

        mShowProgress = new MutableLiveData<Boolean>();
        mShowProgress.setValue(true);

        // getDrivers();
    }

    public void loadDrivers(boolean forceUpdate, final boolean showLoadingUI) {
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
                    fetchDrivers();
                    return;
                }

                List<Driver> list = mDrivers.getValue();
                if (list == null) {
                    list = drivers;
                } else {
                    for (Driver d : drivers) {
                        list.add(d);
                    }
                }

                mDrivers.setValue(list);

                if (showLoadingUI) {
                    mShowProgress.setValue(false);
                }

                mQueryDone.setValue(true);
            }

            @Override
            public void onDataNotAvailable() {
                fetchDrivers();
            }
        });
    }

    @Override
    public LiveData<List<Driver>> getDrivers() {
        if (mDrivers == null) {
            mDrivers = new MutableLiveData<List<Driver>>();
            loadDrivers(false, true);
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
    public LiveData<Boolean> getEndOfDrivers() {
        return mEndOfDrivers;
    }

    @Override
    public void refresh() {
        mPage = 0;
        Utils.writePagePref(getApplication().getApplicationContext(), 0);
        Utils.writeEndOfDriversPref(getApplication().getApplicationContext(), false);
        setEndOfDrivers(false);

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
        if (mEndOfDrivers.getValue()) {
            Toast.makeText(getApplication().getApplicationContext(),
                    R.string.no_more_drivers, Toast.LENGTH_SHORT).show();
            return;
        }

        // loadDrivers(false, true);

        fetchDrivers();
    }

    private void fetchDrivers() {
        setQueryDone(false);

        Runnable runnable = () -> {
            //StringRequest req = mRequestFetcher.getRequest(mPage);
            JsonRequest req = mRequestFetcher.getJsonRequest(mPage);
            mNetworkQueue.addToRequestQueue(req);
        };

        mExecutors.networkIO().execute(runnable);
    }

    @Override
    public void doOnQueryDone(ItemResponse response) {
        if (response == null) {
            mExecutors.mainThread().execute(setParamsAfterQueryDone());
            return;
        }

        List<Driver> list = mDrivers.getValue();
        if (list == null) {
            list = new ArrayList<Driver>();
        }

        int page = (Integer.parseInt(response.getMRData().getOffset()) / 10);

        List<DriverStub> drivers = response.getMRData().getDriverTable().getDrivers();
        if ((drivers != null) && (drivers.size() > 0)) {
            for (DriverStub ds : drivers) {
                Driver d = new Driver(ds.getDriverId(), ds.getUrl(), ds.getGivenName(),
                        ds.getFamilyName(), ds.getDateOfBirth(), ds.getNationality(), page);

                mRepository.saveDriver(d);
                list.add(d);
            }

            // Page maintenance
            mPage++;
            Utils.writePagePref(getApplication().getApplicationContext(), mPage);

            if (list.size() < (Utils.MIN_PAGES_CONSTANT * Utils.ITEMS_PER_PAGE_CONSTANT)) {
                loadNextSetUsers();
            }

            mExecutors.mainThread().execute(setDrivers(list));
        } else {
            if (Integer.parseInt(response.getMRData().getOffset()) >=
                    Integer.parseInt(response.getMRData().getTotal())) {
                // TODO: Need to optimize context switching
                mExecutors.mainThread().execute(setEndOfDrivers(true));
                Utils.writeEndOfDriversPref(getApplication().getApplicationContext(), true);
                mExecutors.mainThread().execute(showToast(R.string.no_more_drivers));
            }
        }

        mExecutors.mainThread().execute(setParamsAfterQueryDone());
    }

    private Runnable setEndOfDrivers(boolean val) {
        return () -> {
            mEndOfDrivers.setValue(val);
        };
    }

    private Runnable showToast(final int id) {
        return () -> {
            Toast.makeText(getApplication().getApplicationContext(),
                    R.string.no_more_drivers, Toast.LENGTH_SHORT).show();
        };
    }

    private Runnable setParamsAfterQueryDone() {
        return () -> {
            mQueryDone.setValue(true);
            //mShowProgress.setValue(false);
        };
    }

    private Runnable setQueryDone(boolean val) {
        return () -> {
            mQueryDone.setValue(val);
            //mShowProgress.setValue(false);
        };
    }

    @NonNull
    private Runnable setDrivers(final List<Driver> list) {
        return () -> mDrivers.setValue(list);
    }

    @Override
    public void doOnError() {
        // TODO: Show some error
        mExecutors.mainThread().execute(setParamsAfterQueryDone());
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
        mRepository = null;
        mExecutors = null;
    }

    public void clearDrivers() {
        List<Driver> list = mDrivers.getValue();
        list.clear();
        mDrivers.setValue(list);
    }

}
