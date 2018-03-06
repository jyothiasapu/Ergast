package com.jyothi.ergast.adapter;

import android.view.ViewGroup;

import com.a4direct.blanket.adapter.GenericAdapter;
import com.a4direct.blanket.adapter.base.Destroy;
import com.a4direct.blanket.adapter.base.OnItemClickListener;
import com.jyothi.ergast.R;
import com.jyothi.ergast.data.Driver;
import com.jyothi.ergast.network.LoadingState;
import com.jyothi.ergast.viewholder.DriverViewHolder;

/**
 * Created by Jyothi Asapu on 25-01-2018.
 */

public class DriverAdapter extends GenericAdapter<Driver, OnItemClickListener<Driver>, DriverViewHolder>
        implements Destroy {

    private LoadingState mLoadingState;

    @Override
    public DriverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DriverViewHolder(inflate(R.layout.list_item, parent));
    }

    private boolean hasExtraRow() {
        if (mLoadingState != null && mLoadingState != LoadingState.LOADED
                && mLoadingState != LoadingState.MAXPAGE) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.list_item;
        }
    }
}
