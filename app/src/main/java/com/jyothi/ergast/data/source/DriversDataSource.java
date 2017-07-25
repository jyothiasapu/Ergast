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

import java.util.List;

/**
 * Main entry point for accessing drivers data.
 * <p>
 * For simplicity, only getDrivers() and getDriver() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * <p>
 * For example, when a new driver is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface DriversDataSource {

    interface LoadDriversCallback {

        void onDriversLoaded(List<Driver> drivers);

        void onDataNotAvailable();
    }

    interface GetDriverCallback {

        void onDriverLoaded(List<Driver> driver);

        void onDataNotAvailable();
    }

    void getDrivers(@NonNull LoadDriversCallback callback);

    void getDrivers(@NonNull int page, @NonNull LoadDriversCallback callback);

    void getDriver(@NonNull String id, @NonNull GetDriverCallback callback);

    void saveDriver(@NonNull Driver driver);

    void refreshDrivers();

    void deleteAllDrivers();

}
