package com.jyothi.ergast.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Utils {

    private static final String TAG = "Utils";
    private static final String PREFERENCE_FILE = "Jyothi";

    public static final String PAGE_PREFERENCE = "page_pref";
    public static final String END_OF_DRIVERS_PREFERENCE = "end_of_drivers_pref";

    public static void writeIntPref(Context ctx, String key,
                                    int val) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, val);
        editor.commit();
    }

    public static int readIntPref(Context ctx, String key, int def) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, def);
    }

    public static void writeBoolPref(Context ctx, String key, boolean val) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, val);
        editor.commit();
    }

    public static boolean readBoolPref(Context ctx, String key) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, false);
    }

    public static void writePagePref(Context ctx, int val) {
        writeIntPref(ctx, PAGE_PREFERENCE, val);
    }

    public static int readPagePref(Context ctx, int def) {
        return readIntPref(ctx, PAGE_PREFERENCE, def);
    }

    public static void writeEndOfDriversPref(Context ctx, boolean val) {
        writeBoolPref(ctx, END_OF_DRIVERS_PREFERENCE, val);
    }

    public static boolean readEndOfDriversPref(Context ctx) {
        return readBoolPref(ctx, END_OF_DRIVERS_PREFERENCE);
    }

}
