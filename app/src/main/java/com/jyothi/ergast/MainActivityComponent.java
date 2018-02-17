package com.jyothi.ergast;

import com.jyothi.ergast.di.MainActivityModule;
import com.jyothi.ergast.di.MainActivityScope;

import dagger.Component;

/**
 * Created by Jyothi on 16-02-2018.
 */

@MainActivityScope
@Component(modules = MainActivityModule.class)
public interface MainActivityComponent {

    public void inject(MainActivity act);
}
