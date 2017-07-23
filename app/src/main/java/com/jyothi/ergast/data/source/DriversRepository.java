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

package com.jyothi.ergast.data.source;

import android.support.annotation.NonNull;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.interfaces.Destroy;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load tasks from the data sources into a cache.
 * <p>
 * For simplicity, this implements a dumb synchronisation between locally persisted data and data
 * obtained from the server, by using the remote data source only if the local database doesn't
 * exist or is empty.
 */
public class DriversRepository implements DriversDataSource, Destroy {

    private static DriversRepository INSTANCE = null;

    private final DriversDataSource mDriversLocalDataSource;

    // Prevent direct instantiation.
    private DriversRepository(@NonNull DriversDataSource tasksLocalDataSource) {
        mDriversLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param tasksLocalDataSource the device storage data source
     * @return the {@link DriversRepository} instance
     */
    public static DriversRepository getInstance(DriversDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DriversRepository(tasksLocalDataSource);
        }

        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(DriversDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * Gets tasks from cache, local data source (SQLite), whichever is
     * available first.
     * <p>
     * Note: {@link LoadDriversCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getDrivers(@NonNull final LoadDriversCallback callback) {
        checkNotNull(callback);

        // Query the local storage if available. If not, query the network.
        mDriversLocalDataSource.getDrivers(new LoadDriversCallback() {

            @Override
            public void onDriversLoaded(List<Driver> drivers) {
                callback.onDriversLoaded(drivers);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }

    @Override
    public void saveDriver(@NonNull Driver driver) {
        checkNotNull(driver);
        mDriversLocalDataSource.saveDriver(driver);
    }

    /**
     * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source. This is done to simplify the sample.
     * <p>
     * Note: {@link GetDriverCallback#onDataNotAvailable()} is fired if both data sources fail to
     * get the data.
     */
    @Override
    public void getDriver(@NonNull final String driverId, @NonNull final GetDriverCallback callback) {
        checkNotNull(driverId);
        checkNotNull(callback);

        // Is the task in the local data source? If not, query the network.
        mDriversLocalDataSource.getDriver(driverId, new GetDriverCallback() {
            @Override
            public void onDriverLoaded(List<Driver> driver) {
                callback.onDriverLoaded(driver);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void refreshDrivers() {
        deleteAllDrivers();
    }

    @Override
    public void deleteAllDrivers() {
        mDriversLocalDataSource.deleteAllDrivers();
    }

    @Override
    public void tearDown() {

    }
}
