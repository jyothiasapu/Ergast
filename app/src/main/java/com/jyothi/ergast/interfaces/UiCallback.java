package com.jyothi.ergast.interfaces;

/**
 * Created by Jyothi on 7/22/2017.
 */

public interface UiCallback {

    public void startProgressDialog();

    public void stopProgressDialog();

    public void isActive();

    public void showErrorOnLoading();

    public void onQueryFinished();

}
