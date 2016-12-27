package com.example.daniel.chatroomapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Daniel on 02/12/2016.
 */

public class VolleyQueueSingleton {

    private static VolleyQueueSingleton mInstance;
    private static Context mCtx;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private VolleyQueueSingleton(Context context){
        mCtx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

    }

    private RequestQueue getRequestQueue(){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        return requestQueue;
    }

    public ImageLoader getImageLoader(){

        return imageLoader;
    }

    public static synchronized VolleyQueueSingleton getmInstance(Context context){

        if (mInstance == null)
            mInstance = new VolleyQueueSingleton(context);

        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){

        getRequestQueue().add(request);
    }

    //ADDIMAGE DOWWNLOADER


}
