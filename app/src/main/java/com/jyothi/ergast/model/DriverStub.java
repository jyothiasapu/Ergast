package com.jyothi.ergast.model;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class DriverStub {

    private String driverId;
    private String url;
    private String givenName;
    private String familyName;
    private String dateOfBirth;
    private String nationality;

    public DriverStub(String dId, String url, String gName, String fName,
                      String dob, String nati) {
        this.driverId = dId;
        this.url = url;
        this.givenName = gName;
        this.familyName = fName;
        this.dateOfBirth = dob;
        this.nationality = nati;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getUrl() {
        return url;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNationality() {
        return nationality;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"driverId\":\"" + driverId + "\",");
        sb.append("\"url\":\"" + url + "\",");
        sb.append("\"givenName\":\"" + givenName + "\",");
        sb.append("\"familyName\":\"" + familyName + "\",");
        sb.append("\"dateOfBirth\":\"" + dateOfBirth + "\",");
        sb.append("\"nationality\":\"" + nationality + "\"}");

        return sb.toString();
    }
}
