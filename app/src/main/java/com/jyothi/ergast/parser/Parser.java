package com.jyothi.ergast.parser;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jyothi.ergast.model.ItemResponse;

import org.json.JSONException;
import org.json.XML;

import java.io.StringReader;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class Parser {

    private static final String TAG = "Parser";

    private String mJsonString;

    public Parser(String resp) {
        mJsonString = resp;
    }

    public ItemResponse parse() throws JSONException {
        fixJson();

        return parseResponse();
    }

    private void fixJson() throws JSONException {
        mJsonString = XML.toJSONObject(mJsonString).toString();

        mJsonString = mJsonString.
                replace("\"MRData\"", "\"mRData\"").
                replace("\"Url\"", "\"url\"").
                replace("\"DriverTable\"", "\"driverTable\"").
                replace("\"Driver\"", "\"driver\"").
                replace("\"Drivers\"", "\"drivers\"").
                replace("\"DriverStub\"", "\"driver\"").
                replace("\"Series\"", "\"series\"").
                replace("\"Limit\"", "\"limit\"").
                replace("\"Offset\"", "\"offset\"").
                replace("\"Total\"", "\"total\"").
                replace("\"DriverId\"", "\"driverId\"").
                replace("\"GivenName\"", "\"givenName\"").
                replace("\"FamilyName\"", "\"familyName\"").
                replace("\"DateOfBirth\"", "\"dateOfBirth\"").
                replace("\"Nationality\"", "\"nationality\"");
    }

    private ItemResponse parseResponse() {
        JsonReader jReader = new JsonReader(
                new StringReader(mJsonString));
        jReader.setLenient(true);

        Gson gson = new Gson();
        return gson.fromJson(jReader, ItemResponse.class);
    }
}
