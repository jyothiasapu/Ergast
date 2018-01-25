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

package com.jyothi.ergast.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;

import com.jyothi.ergast.data.source.DriversRepository;
import com.jyothi.ergast.data.source.local.DriversLocalDataSource;
import com.jyothi.ergast.data.source.local.ErgastDatabase;

import java.util.ArrayList;


/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        if (fragment == null) {
            return;
        }

        if (fragmentManager == null) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static DriversRepository provideErgastRepository(@NonNull Context context, AppExecutors executors) {
        if (context == null) {
            return null;
        }

        ErgastDatabase database = ErgastDatabase.getInstance(context);
        return DriversRepository.getInstance(DriversLocalDataSource.getInstance(executors,
                database.driverDao()));
    }

    public static boolean requestPermissions(Context ctx, int returnCode, ArrayList<String> perms) {
        if (perms.size() == 0) {
            return true;
        }

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        ArrayList<String> list = new ArrayList<String>();

        for (String in : perms) {
            checkPermission(ctx, in, list);
        }

        if (!list.isEmpty()) {
            ActivityCompat.requestPermissions(((Activity) ctx),
                    list.toArray(new String[list.size()]),
                    returnCode);
            return false;
        } else {
            return true;
        }
    }

    public static void checkPermission(Context ctx, String perm, ArrayList<String> list) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ctx, perm)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            list.add(perm);
        }
    }

}
