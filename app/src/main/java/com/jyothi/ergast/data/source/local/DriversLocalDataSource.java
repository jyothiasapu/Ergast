/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jyothi.ergast.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.data.source.DriversDataSource;
import com.jyothi.ergast.interfaces.Destroy;
import com.jyothi.ergast.util.AppExecutors;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 */
public class DriversLocalDataSource implements DriversDataSource, Destroy {

    private static volatile DriversLocalDataSource INSTANCE;

    private DriversDao mDriversDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private DriversLocalDataSource(@NonNull AppExecutors appExecutors,
                                   @NonNull DriversDao tasksDao) {
        mAppExecutors = appExecutors;
        mDriversDao = tasksDao;
    }

    public static DriversLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                     @NonNull DriversDao tasksDao) {
        if (INSTANCE == null) {
            synchronized (DriversLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DriversLocalDataSource(appExecutors, tasksDao);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Note: {@link LoadDriversCallback#onDataNotAvailable()} is fired if the database doesn't exist
     * or the table is empty.
     */
    @Override
    public void getDrivers(@NonNull final LoadDriversCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Driver> drivers = mDriversDao.getDrivers();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (drivers.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onDriversLoaded(drivers);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getDrivers(@NonNull int page, @NonNull LoadDriversCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Driver> drivers = mDriversDao.getDrivers(page);
                mAppExecutors.mainThread().execute(new Runnable() {

                    @Override
                    public void run() {
                        if (drivers.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onDataNotAvailable();
                        } else {
                            callback.onDriversLoaded(drivers);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);

    }

    /**
     * Note: {@link GetDriverCallback#onDataNotAvailable()} is fired if the {@link Driver} isn't
     * found.
     */
    @Override
    public void getDriver(@NonNull final String driverId, @NonNull final GetDriverCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Driver> driver = mDriversDao.getDriverById(driverId);

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (driver != null) {
                            callback.onDriverLoaded(driver);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveDriver(@NonNull final Driver task) {
        checkNotNull(task);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mDriversDao.insertDriver(task);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshDrivers() {
        // Not required because the {@link DriversRepository} handles the logic of refreshing the
        // drivers from all the available data sources.
    }

    @Override
    public void deleteAllDrivers() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mDriversDao.deleteDrivers();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @VisibleForTesting
    static void clearInstance() {
        INSTANCE = null;
    }

    @Override
    public void tearDown() {
        INSTANCE = null;
    }
}
