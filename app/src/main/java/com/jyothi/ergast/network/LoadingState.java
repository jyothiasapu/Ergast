package com.jyothi.ergast.network;

/**
 * Created by Jyothi on 22-02-2018.
 */

public class LoadingState {

    private final CallStatus status;
    private final String msg;

    public static final LoadingState LOADED;
    public static final LoadingState LOADING;
    public static final LoadingState MAXPAGE;

    public LoadingState(CallStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED = new LoadingState(CallStatus.SUCCESS, "Success");
        LOADING = new LoadingState(CallStatus.RUNNING, "Running");
        MAXPAGE = new LoadingState(CallStatus.MAX, "No More page");
    }

    public CallStatus getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
