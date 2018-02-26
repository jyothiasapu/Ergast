package com.jyothi.ergast;

import android.app.Application;

import com.jyothi.ergast.di.ErgastScope;
import com.jyothi.ergast.di.builder.ActivityBuilder;
import com.jyothi.ergast.di.module.AppModule;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by Jyothi on 16-02-2018.
 */

@ErgastScope
@Component(modules = {AndroidInjectionModule.class, AppModule.class, ActivityBuilder.class})
public interface ErgastComponent {

    void inject(Ergast app);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        ErgastComponent build();
    }
}
