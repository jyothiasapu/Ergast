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


public class DriversRepository implements DriversDataSource, Destroy {

    private static DriversRepository INSTANCE = null;

    private final DriversDataSource mDriversLocalDataSource;

    // Prevent direct instantiation.
    private DriversRepository(@NonNull DriversDataSource tasksLocalDataSource) {
        mDriversLocalDataSource = tasksLocalDataSource;
    }


    public static DriversRepository getInstance(DriversDataSource tasksLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new DriversRepository(tasksLocalDataSource);
        }

        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getDrivers(@NonNull final LoadDriversCallback callback) {
        if (callback == null) {
            return;
        }

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
    public void getDrivers(@NonNull int page, final @NonNull LoadDriversCallback callback) {
        // Query the local storage if available. If not, query the network.
        mDriversLocalDataSource.getDrivers(page, new LoadDriversCallback() {

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
        if (driver == null) {
            return;
        }
        mDriversLocalDataSource.saveDriver(driver);
    }

    @Override
    public void getDriver(@NonNull final String driverId, @NonNull final GetDriverCallback callback) {
        if (driverId == null) {
            return;
        }
        if (callback == null) {
            return;
        }

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
