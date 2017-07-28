package com.jyothi.ergast.model;

import java.util.List;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class DriverTable {

    private List<DriverStub> drivers;

    public DriverTable(List<DriverStub> list) {
        this.drivers = list;
    }

    public List<DriverStub> getDrivers() {
        return drivers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\"drivers\":[");

        for (DriverStub r : drivers) {
            sb.append(r.toString());
            sb.append(",");
        }

        if (drivers.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("]");

        return sb.toString();
    }
}
