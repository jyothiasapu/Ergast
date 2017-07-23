package com.jyothi.ergast.model;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class MRData {

    private String xmlns;
    private String series;
    private String url;
    private String limit;
    private String offset;
    private String total;
    private DriverTable driverTable;

    public MRData(String xmlns, String series, String url, String limit,
                  String offset, String total, DriverTable table) {
        this.xmlns = xmlns;
        this.series = series;
        this.url = url;
        this.limit = limit;
        this.offset = offset;
        this.total = total;
        this.driverTable = table;
    }

    public String getXmlns() {
        return xmlns;
    }

    public String getSeries() {
        return series;
    }

    public String getUrl() {
        return url;
    }

    public String getLimit() {
        return limit;
    }

    public String getOffset() {
        return offset;
    }

    public String getTotal() {
        return total;
    }

    public DriverTable getDriverTable() {
        return driverTable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"xmlns\":\"" + xmlns + "\",");
        sb.append("\"series\":\"" + series + "\",");
        sb.append("\"url\":\"" + url + "\",");
        sb.append("\"limit\":\"" + limit + "\",");
        sb.append("\"offset\":\"" + offset + "\",");
        sb.append("\"total\":\"" + total + "\",");
        sb.append("\"driverTable\":{" + driverTable.toString() + "}");

        return sb.toString();
    }

}
