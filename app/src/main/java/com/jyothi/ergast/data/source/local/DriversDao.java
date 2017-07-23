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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.jyothi.ergast.data.Driver;

import java.util.List;

/**
 * Data Access Object for the tasks table.
 */
@Dao
public interface DriversDao {

    /**
     * Select all drivers from the drivers table.
     *
     * @return all drivers.
     */
    @Query("SELECT * FROM Drivers")
    List<Driver> getDrivers();

    /**
     * Select a task by id.
     *
     * @param id the task id.
     * @return the task with taskId.
     */
    @Query("SELECT * FROM Drivers WHERE driver_id LIKE :id")
    List<Driver> getDriverById(String id);

    /**
     * Insert a task in the database. If the task already exists, replace it.
     *
     * @param task the task to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDriver(Driver task);

    /**
     * Delete all tasks.
     */
    @Query("DELETE FROM Drivers")
    void deleteDrivers();

}
