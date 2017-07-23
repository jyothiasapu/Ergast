package com.jyothi.ergast.interfaces;

import com.jyothi.ergast.model.ItemResponse;

/**
 * Created by Jyothi on 7/22/2017.
 */

public interface NetworkCallback {

    public void doOnQueryDone(ItemResponse response);

    public void doOnError();
}
