package com.jyothi.ergast.di;

import android.support.v7.widget.LinearLayoutManager;

import com.jyothi.ergast.ItemAdapter;
import com.jyothi.ergast.MainActivity;
import com.jyothi.ergast.data.Driver;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module
public class MainActivityModule {

    private final MainActivity mainActivity;

    public MainActivityModule(MainActivity act) {
        mainActivity = act;
    }

    @Provides
    @MainActivityScope
    public ItemAdapter getItemAdapter() {
        return new ItemAdapter(new ArrayList<Driver>());
    }

    @Provides
    @MainActivityScope
    public LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(mainActivity);
    }
}
