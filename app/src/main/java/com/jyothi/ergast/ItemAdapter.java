package com.jyothi.ergast;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.interfaces.Destroy;
import com.jyothi.ergast.interfaces.ItemAdapterInterface;

import java.util.List;

/**
 * Created by Jyothi on 7/22/16.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.CustomViewHolder>
        implements ItemAdapterInterface, Filterable, Destroy {

    private List<Driver> mItems;
    private DriverFilter mDriverFilter = null;

    public ItemAdapter(List<Driver> items) {
        this.mItems = items;
    }

    @Override
    public void setItems(List<Driver> list) {
        mItems = list;
        notifyDataSetChanged();
    }

    @Override
    public void clearItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int i) {
        Driver driver = mItems.get(i);

        holder.mNationality.setText(driver.getNationality());
        holder.mGivenName.setText(driver.getGivenName());
        holder.mUrl.setText(driver.getUrl());
        holder.mDob.setText(driver.getDob());
        holder.mDriverId.setText(driver.getDriverId());
        holder.mFamilyName.setText(driver.getFamilyName());
    }

    @Override
    public int getItemCount() {
        return (null != mItems ? mItems.size() : 0);
    }

    @Override
    public Filter getFilter() {
        if (mDriverFilter == null) {
            mDriverFilter = new DriverFilter(mItems, this);
        }

        return mDriverFilter;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        public TextView mNationality;
        public TextView mGivenName;
        public TextView mUrl;
        public TextView mDob;
        public TextView mDriverId;
        public TextView mFamilyName;

        public CustomViewHolder(View view) {
            super(view);

            mNationality = (TextView) view.findViewById(R.id.nationality);
            mGivenName = (TextView) view.findViewById(R.id.given_name);
            mUrl = (TextView) view.findViewById(R.id.url);
            mDob = (TextView) view.findViewById(R.id.dob);
            mDriverId = (TextView) view.findViewById(R.id.driver_id);
            mFamilyName = (TextView) view.findViewById(R.id.family_name);
        }
    }

    @Override
    public void clearFilter() {
        if (mDriverFilter != null) {
            mDriverFilter.tearDown();
            mDriverFilter = null;
        }
    }

    @Override
    public void tearDown() {
        if (mItems != null) {
            mItems.clear();
            mItems = null;
        }

        clearFilter();
    }
}
