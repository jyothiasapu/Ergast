package com.jyothi.ergast;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.data.source.DriversDataSource;
import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.interfaces.MainViewInterface;
import com.jyothi.ergast.interfaces.NetworkCallback;
import com.jyothi.ergast.model.DriverStub;
import com.jyothi.ergast.model.DriverTable;
import com.jyothi.ergast.model.ItemResponse;
import com.jyothi.ergast.model.MRData;
import com.jyothi.ergast.network.ApiService;
import com.jyothi.ergast.network.RetroClient;
import com.jyothi.ergast.util.ActivityUtils;
import com.jyothi.ergast.util.AppExecutors;
import com.jyothi.ergast.util.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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

    public MainViewModel(Application app) {
        super(app);

        mEndOfDrivers = new MutableLiveData<Boolean>();
        mEndOfDrivers.setValue(false);

        mExecutors = ((Ergast) app).getExecutors();
        mRepository = ActivityUtils.provideErgastRepository(app.getApplicationContext(),
                mExecutors);

        mQueryDone = new MutableLiveData<Boolean>();
        mQueryDone.setValue(true);

        mShowProgress = new MutableLiveData<Boolean>();
        mShowProgress.setValue(true);
    }

    public void loadDrivers() {
        mExecutors.mainThread().execute(setParamsAfterQueryDone(false));

        mRepository.getDrivers(mPage, new DriversDataSource.LoadDriversCallback() {

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

                mPage++;

                mExecutors.mainThread().execute(setParamsAfterQueryDone(true));
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
            loadDrivers();
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
                if (list != null) {
                    list.clear();
                    mDrivers.setValue(list);
                } else {
                    mDrivers.setValue(new ArrayList<Driver>());
                }

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

        loadDrivers();

        // fetchDrivers();
    }

    private void fetchDrivers() {
        ApiService api = RetroClient.getApiService();

        Call<ItemResponse> call = api.getDrivers(mPage * Utils.ITEMS_PER_PAGE_CONSTANT);
        call.enqueue(new Callback<ItemResponse>() {

            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                doOnQueryDone(response.body());
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {
                Log.i(TAG, "Response : " + call.toString());
            }
        });
    }

    @Override
    public void doOnQueryDone(ItemResponse response) {
        if (response == null) {
            mExecutors.mainThread().execute(setParamsAfterQueryDone(true));
            return;
        }

        MRData mrData = response.getMRData();
        if (mrData == null) {
            return;
        }

        DriverTable dt = mrData.getDriverTable();
        if (dt == null) {
            return;
        }

        List<Driver> list = mDrivers.getValue();
        if (list == null) {
            list = new ArrayList<Driver>();
        }

        int page = (Integer.parseInt(response.getMRData().getOffset()) / 10);

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

            if (list.size() < (Utils.MIN_PAGES_CONSTANT * Utils.ITEMS_PER_PAGE_CONSTANT)) {
                loadNextSetUsers();
            }

            mExecutors.mainThread().execute(setDrivers(list));
        } else {
            if (Integer.parseInt(response.getMRData().getOffset()) >=
                    Integer.parseInt(response.getMRData().getTotal())) {
                //Utils.writeEndOfDriversPref(getApplication().getApplicationContext(), true);

                mExecutors.mainThread().execute(setEndOfDrivers(true));
            } else {
                mExecutors.mainThread().execute(setParamsAfterQueryDone(true));
            }
        }
    }

    private Runnable setEndOfDrivers(final boolean val) {
        return new Runnable() {
            @Override
            public void run() {
                mEndOfDrivers.setValue(val);
                setQueryDone(true);

                if (val) {
                    Toast.makeText(getApplication().getApplicationContext(),
                            R.string.no_more_drivers, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private Runnable setParamsAfterQueryDone(final boolean val) {
        return new Runnable() {
            @Override
            public void run() {
                setQueryDone(val);
            }
        };
    }

    private void setQueryDone(final boolean val) {
        mQueryDone.setValue(val);
        mShowProgress.setValue(!val);
    }

    @NonNull
    private Runnable setDrivers(final List<Driver> list) {
        return new Runnable() {
            @Override
            public void run() {
                mDrivers.setValue(list);

                setQueryDone(true);
            }
        };
    }

    @Override
    public void doOnError() {
        // TODO: Show some error
        mExecutors.mainThread().execute(setParamsAfterQueryDone(true));
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (mDrivers != null) {
            mDrivers.setValue(null);
            mDrivers = null;
        }

        mRepository = null;
        mExecutors = null;
    }

    public void clearDrivers() {
        List<Driver> list = mDrivers.getValue();
        list.clear();
        mDrivers.setValue(list);
    }

}
