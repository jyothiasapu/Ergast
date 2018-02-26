package com.jyothi.ergast.interfaces;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.v7.app.AppCompatActivity;

import com.jyothi.ergast.data.Driver;

/**
 * Created by Jyothi on 7/23/2017.
 */

public interface MainViewInterface {

    public LiveData<PagedList<Driver>> getDrivers();

    public void removeDriversObserver(AppCompatActivity act);

    public void refresh();
}
