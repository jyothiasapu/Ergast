/*
 * Copyright 2017, The Android Open Source Project
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

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jyothi.ergast.data.Driver;

import java.util.List;

@Dao
public interface DriversDao {

    @Query("SELECT * FROM Drivers")
    List<Driver> getDrivers();

    @Query("SELECT * FROM Drivers WHERE page = :p")
    List<Driver> getDrivers(int p);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDriver(Driver task);

    @Query("DELETE FROM Drivers")
    void deleteDrivers();

    @Query("SELECT * FROM Drivers WHERE driver_id LIKE :id")
    public abstract DataSource.Factory<Integer, Driver> getDrivers(String id);

}
