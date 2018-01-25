package com.jyothi.ergast.adapter;

import android.view.ViewGroup;

import com.jyothi.ergast.DriverViewHolder;
import com.jyothi.ergast.R;
import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.interfaces.OnObjectClickListener;

/**
 * Created by Jyothi Asapu on 25-01-2018.
 */

public class DriverAdapter extends GenericAdapter<Driver, OnObjectClickListener<Driver>, DriverViewHolder> {
    @Override
    public DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DriverViewHolder(inflate(R.layout.list_item, parent));
    }
}
