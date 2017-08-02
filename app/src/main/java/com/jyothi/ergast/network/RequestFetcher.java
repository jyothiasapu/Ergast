package com.jyothi.ergast.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jyothi.ergast.interfaces.Destroy;
import com.jyothi.ergast.interfaces.NetworkCallback;
import com.jyothi.ergast.parser.Parser;
import com.jyothi.ergast.util.AppExecutors;
import com.jyothi.ergast.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jyothi on 7/22/2017.
 */

public class RequestFetcher implements Destroy {

    private static final String TAG = "RequestFetcher";

    private final String URL = "http://ergast.com/api/f1/drivers.json?limit=" + Utils.ITEMS_PER_PAGE_CONSTANT;
    private final String URL_EXTN = "&offset=";

    private NetworkCallback mCallback;
    private AppExecutors mExecutors;

    public RequestFetcher(NetworkCallback l, AppExecutors exe) {
        mCallback = l;
        mExecutors = exe;
    }

    public String createUrl(int page) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL);

        if (page != 0) {
            sb.append(URL_EXTN + (page * Utils.ITEMS_PER_PAGE_CONSTANT));
        }

        Log.d(TAG, "Query : " + sb.toString());

        return sb.toString();
    }

    /**
     * Creates network request.
     *
     * @return StringRequest
     */
    public StringRequest getRequest(int page) {
        StringRequest sr = new StringRequest(Request.Method.GET,
                createUrl(page),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mExecutors.networkIO().execute(parseResponse(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mCallback.doOnError();
                    }
                });

        sr.setShouldCache(true);

        return sr;
    }

    private Runnable parseResponse(final String response) {
        return () -> {
            try {
                Parser p = new Parser(response);
                mCallback.doOnQueryDone(p.parse());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * Creates json based network request.
     *
     * @return StringRequest
     */
    public JsonObjectRequest getJsonRequest(int page) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (createUrl(page), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mExecutors.networkIO().execute(parseResponse(response.toString()));
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mCallback.doOnError();
                    }
                });

        return jsObjRequest;
    }

    @Override
    public void tearDown() {
        mCallback = null;
        mExecutors = null;
    }
}
