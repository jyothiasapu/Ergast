package com.jyothi.ergast.di.builder;

import com.jyothi.ergast.MainActivity;
import com.jyothi.ergast.di.module.MainActivityModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by jasapu on 26-02-2018.
 */

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = {
            MainActivityModule.class
    })
    abstract MainActivity bindMainActivity();
}
