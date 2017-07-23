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

package com.jyothi.ergast.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Model class for a Driver.
 */
@Entity(tableName = "drivers")
public final class Driver {

    @PrimaryKey
    @ColumnInfo(name = "entryid")
    public String id;

    @Nullable
    @ColumnInfo(name = "driver_id")
    public String driverId;

    @Nullable
    @ColumnInfo(name = "url")
    public String url;

    @Nullable
    @ColumnInfo(name = "given_name")
    public String givenName;

    @Nullable
    @ColumnInfo(name = "family_name")
    public String familyName;

    @Nullable
    @ColumnInfo(name = "dateofbirth")
    public String dob;

    @Nullable
    @ColumnInfo(name = "nationality")
    public String nationality;

    /**
     * Use this constructor to specify a completed Driver. If the Driver already has an id (copy of
     * another Driver).
     *
     * @param driverId   driverId of the driver
     * @param url        url of the driver
     * @param givenName  given name of the driver
     * @param familyName family name of the driver
     * @param dob        date of birth of the driver
     * @param nat        nationality of the driver
     */
    @Ignore
    public Driver(@Nullable String driverId, @Nullable String url,
                  @Nullable String givenName, @Nullable String familyName,
                  @Nullable String dob, @Nullable String nat) {
        this(UUID.randomUUID().toString(), driverId, url, givenName, familyName, dob, nat);
    }

    /**
     * Use this constructor to specify a completed Driver. If the Driver already has an id (copy of
     * another Driver).
     *
     * @param id         id of the driver
     * @param driverId   driverId of the driver
     * @param url        url of the driver
     * @param givenName  given name of the driver
     * @param familyName family name of the driver
     * @param dob        date of birth of the driver
     * @param nat        nationality of the driver
     */
    public Driver(@NonNull String id, @Nullable String driverId, @Nullable String url,
                  @Nullable String givenName, @Nullable String familyName,
                  @Nullable String dob, @Nullable String nat) {
        this.id = id;
        this.driverId = driverId;
        this.url = url;
        this.givenName = givenName;
        this.familyName = familyName;
        this.dob = dob;
        this.nationality = nat;
    }

    public Driver() {
        this.id = UUID.randomUUID().toString();
    }

    @NonNull
    public String getId() {
        return id;
    }

    @Nullable
    public String getDriverId() {
        return driverId;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getGivenName() {
        return givenName;
    }

    @Nullable
    public String getFamilyName() {
        return familyName;
    }

    @Nullable
    public String getDob() {
        return dob;
    }

    @Nullable
    public String getNationality() {
        return nationality;
    }

    public void setDriverId(@Nullable String driverId) {
        this.driverId = driverId;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    public void setGivenName(@Nullable String givenName) {
        this.givenName = givenName;
    }

    public void setFamilyName(@Nullable String familyName) {
        this.familyName = familyName;
    }

    public void setDob(@Nullable String dob) {
        this.dob = dob;
    }

    public void setNationality(@Nullable String nationality) {
        this.nationality = nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Driver task = (Driver) o;
        return Objects.equal(id, task.id) &&
                Objects.equal(driverId, task.driverId) &&
                Objects.equal(url, task.url) &&
                Objects.equal(givenName, task.givenName) &&
                Objects.equal(familyName, task.familyName) &&
                Objects.equal(driverId, task.driverId) &&
                Objects.equal(nationality, task.nationality);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, driverId, url, givenName, familyName, dob, nationality);
    }

    @Override
    public String toString() {
        return "Driver with title " + driverId;
    }
}
