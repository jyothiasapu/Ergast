package com.jyothi.ergast.interfaces;


import com.jyothi.ergast.model.ItemResponse;

/**
 * Created by Jyothi on 7/22/2017.
 */

public interface NetworkCall {

    public void doQuery(int num);

    public ItemResponse getResponse();

    public void resetQueryFlag();

    public void setListener(NetworkCallback l);

    public void setResponse(ItemResponse response);
}
