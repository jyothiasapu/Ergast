package com.jyothi.ergast.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.jyothi.ergast.interfaces.Destroy;

/**
 * Created by Jyothi on 7/22/2017.
 */
public class NetworkQueue implements Destroy {

    private static final String TAG = "NetworkQueue";

    private static NetworkQueue mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private NetworkQueue(Context ctx) {
        mRequestQueue = getRequestQueue(ctx);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(50);

                    @Override
                    public Bitmap getBitmap(String url) {
                        Log.d(TAG, "getting bitmap");
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        Log.d(TAG, "Adding bitmap");
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkQueue(context.getApplicationContext());
        }

        return mInstance;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private RequestQueue getRequestQueue(Context ctx) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        Log.i(TAG, "addToRequestQueue");

        if (mRequestQueue != null) {
            mRequestQueue.add(req);
        }
    }

    public void cancelByTag(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    @Override
    public void tearDown() {
        if (mRequestQueue != null) {
            mRequestQueue.stop();
            mRequestQueue = null;
        }

        mInstance = null;
    }
}
