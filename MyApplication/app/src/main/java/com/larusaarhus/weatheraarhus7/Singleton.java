package com.larusaarhus.weatheraarhus7;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by brorbw on 04/05/16.
 */
public class Singleton {
    private static RequestQueue requestQueue = null;

    public static RequestQueue getInstance(Context context) {
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

}
