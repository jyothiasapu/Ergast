package com.jyothi.ergast.model;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class ItemResponse {

    private MRData MRData;

    public ItemResponse(MRData data) {
        this.MRData = data;
    }

    public MRData getMRData() {
        return MRData;
    }

}
