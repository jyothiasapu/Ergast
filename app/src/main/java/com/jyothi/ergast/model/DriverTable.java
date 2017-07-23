package com.jyothi.ergast.model;

import java.util.List;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class DriverTable {

    private List<DriverStub> driver;

    public DriverTable(List<DriverStub> list) {
        this.driver = list;
    }

    public List<DriverStub> getDrivers() {
        return driver;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\"driver\":[");

        for (DriverStub r : driver) {
            sb.append(r.toString());
            sb.append(",");
        }

        if (driver.size() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("]");

        return sb.toString();
    }
}
