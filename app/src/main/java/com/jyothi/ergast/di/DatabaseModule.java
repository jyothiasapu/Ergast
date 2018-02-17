package com.jyothi.ergast.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.data.source.local.DriversDao;
import com.jyothi.ergast.data.source.local.DriversLocalDataSource;
import com.jyothi.ergast.data.source.local.ErgastDatabase;
import com.jyothi.ergast.util.AppExecutors;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Jyothi on 16-02-2018.
 */

@Module(includes = {ContextModule.class, ExecutorModule.class})
public class DatabaseModule {

    @Provides
    @ErgastScope
    public DriversLocalDataSource getDriversLocalDataSource(AppExecutors exe, DriversDao dao) {
        return new DriversLocalDataSource(dao, exe);
    }

    @Provides
    @ErgastScope
    public DriversRepository getDriversRepository(DriversLocalDataSource source) {
        return new DriversRepository(source);
    }

    @Provides
    @ErgastScope
    public ErgastDatabase getDatabase(@ApplicationContext Context ctx) {
        return Room.databaseBuilder(ctx, ErgastDatabase.class, "Ergast.db").build();
    }

    @Provides
    @ErgastScope
    public DriversDao getDriversDao(ErgastDatabase database) {
        return database.driverDao();
    }

}
