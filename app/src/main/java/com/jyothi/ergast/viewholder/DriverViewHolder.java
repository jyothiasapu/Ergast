package com.jyothi.ergast.viewholder;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.a4direct.blanket.adapter.base.BaseHolder;
import com.a4direct.blanket.adapter.base.OnItemClickListener;
import com.jyothi.ergast.R;
import com.jyothi.ergast.data.Driver;

/**
 * Created by Jyothi Asapu on 25-01-2018.
 */

public class DriverViewHolder extends BaseHolder<Driver, OnItemClickListener<Driver>> {

    public TextView mNationality;
    public TextView mGivenName;
    public TextView mUrl;
    public TextView mDob;
    public TextView mDriverId;
    public TextView mFamilyName;

    public DriverViewHolder(View itemView) {
        super(itemView);

        mNationality = (TextView) itemView.findViewById(R.id.nationality);
        mGivenName = (TextView) itemView.findViewById(R.id.given_name);
        mUrl = (TextView) itemView.findViewById(R.id.url);
        mDob = (TextView) itemView.findViewById(R.id.dob);
        mDriverId = (TextView) itemView.findViewById(R.id.driver_id);
        mFamilyName = (TextView) itemView.findViewById(R.id.family_name);
    }

    @Override
    public void bindItem(final Driver driver, @Nullable final OnItemClickListener<Driver> listener) {
        mNationality.setText(driver.getNationality());
        mGivenName.setText(driver.getGivenName());
        mUrl.setText(driver.getUrl());
        mDob.setText(driver.getDob());
        mDriverId.setText(driver.getDriverId());
        mFamilyName.setText(driver.getFamilyName());

        if (listener == null) {
            return;
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClicked(driver);
            }
        });
    }

    @Override
    public void clear() {
        mNationality.setText("");
        mGivenName.setText("");
        mUrl.setText("");
        mDob.setText("");
        mDriverId.setText("");
        mFamilyName.setText("");
    }

}
