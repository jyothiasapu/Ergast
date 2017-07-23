package com.jyothi.ergast.interfaces;

import com.jyothi.ergast.data.Driver;

import java.util.List;

/**
 * Created by Jyothi on 23/7/17.
 */

public interface ItemAdapterInterface {

    public void setItems(List<Driver> list);

    public void clearItems();

}
