package com.jyothi.ergast.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jyothi.ergast.interfaces.BaseListener;

/**
 * Created by jasapu on 25-01-2018.
 */

public abstract class BaseHolder<E, L extends BaseListener> extends RecyclerView.ViewHolder {

    public BaseHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindItem(E item, @Nullable L listener);

    public abstract void clear();

}

