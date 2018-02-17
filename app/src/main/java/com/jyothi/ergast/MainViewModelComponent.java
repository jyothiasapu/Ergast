package com.jyothi.ergast;

import com.jyothi.ergast.di.MainViewModelModule;
import com.jyothi.ergast.di.MainViewModelScope;

import dagger.Component;

/**
 * Created by Jyothi on 17-02-2018.
 */

@MainViewModelScope
@Component(modules = MainViewModelModule.class, dependencies = ErgastComponent.class)
public interface MainViewModelComponent {

    public void inject(MainViewModel ctx);
}
