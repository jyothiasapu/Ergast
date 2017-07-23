package com.jyothi.ergast.model;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class ItemResponse {

    private MRData mRData;

    public ItemResponse(MRData data) {
        this.mRData = data;
    }

    public MRData getMRData() {
        return mRData;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"mRData\":{");
        sb.append(mRData.toString());
        sb.append("}}");

        return sb.toString();
    }
}
