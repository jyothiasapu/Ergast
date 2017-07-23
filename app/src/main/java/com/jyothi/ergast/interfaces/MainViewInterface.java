package com.jyothi.ergast.interfaces;

import android.arch.lifecycle.LiveData;

import com.jyothi.ergast.data.Driver;

import java.util.List;

/**
 * Created by Jyothi on 7/23/2017.
 */

public interface MainViewInterface {

    public void getDriverWithDriverId(String driverId);

    public void loadNextSetUsers();

    public LiveData<List<Driver>> getDrivers();

    public LiveData<Boolean> getShowProgress();

    public LiveData<Boolean> getQueryDone();

    public void refresh();
}
