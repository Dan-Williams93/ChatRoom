package com.example.daniel.chatroomapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Daniel on 02/12/2016.
 */

public class VolleyQueueSingleton {

    private static VolleyQueueSingleton mInstance;
    private static Context mCtx;
    private RequestQueue requestQueue;

    private VolleyQueueSingleton(Context context){
        mCtx = context;

        requestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        return requestQueue;
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
