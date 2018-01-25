package com.jyothi.ergast.model;

import java.util.List;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class DriverTable {

    private List<DriverStub> Drivers;

    public DriverTable(List<DriverStub> list) {
        this.Drivers = list;
    }

    public List<DriverStub> getDrivers() {
        return Drivers;
    }

}
