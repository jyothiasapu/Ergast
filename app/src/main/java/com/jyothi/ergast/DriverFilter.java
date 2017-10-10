package com.jyothi.ergast;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.widget.Filter;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.interfaces.Destroy;
import com.jyothi.ergast.interfaces.ItemAdapterInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jyothi on 2/8/17.
 */

public class DriverFilter extends Filter implements Destroy {

    private List<Driver> mDrivers;
    private ItemAdapterInterface mListener;
    private boolean mMarkToDestroy = false;

    private CharSequence mPrevStr = "";

    public DriverFilter(List<Driver> drivers, ItemAdapterInterface l) {
        mDrivers = drivers;

        mListener = l;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        if (constraint == null || constraint.length() == 0) {
            mMarkToDestroy = true;
            results.count = mDrivers.size();
            results.values = mDrivers;
        } else {
            mMarkToDestroy = false;
            List<Driver> filteredList;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filteredList = filterList(constraint.toString());
            } else {
                filteredList = filterListBeforeN(constraint.toString());
            }

            results.count = filteredList.size();
            results.values = filteredList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (mListener != null) {
            mListener.setItems((List<Driver>) results.values);

            if (mMarkToDestroy) {
                mListener.clearFilter();
            }
        }
    }

    private List<Driver> filterListBeforeN(String newText) {
        return filter(newText);
    }

    @NonNull
    private List<Driver> filter(String newText) {
        List<Driver> filteredList = new ArrayList<Driver>();

        newText = newText.toLowerCase();
        for (Driver item : mDrivers) {
            String driverId = item.getDriverId().toLowerCase();
            if (driverId.startsWith(newText)) {
                filteredList.add(item);
            }
        }

        return filteredList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Driver> filterList(String newText) {
        //return mDrivers.stream()
        //        .filter(p -> p.getDriverId().startsWith(newText)).collect(Collectors.toList());

        return filter(newText);
    }

    @Override
    public void tearDown() {
        mListener = null;
        mDrivers = null;
    }
}
