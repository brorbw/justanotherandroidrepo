package com.larusaarhus.weatheraarhusgroup7;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by brorbw on 04/05/16.
 */
public class SQLService extends Service {
    public static boolean isRunning = false;
    private DBHelper dbHelper;
    private long mIndex = 0;
    private String url = "http://api.openweathermap.org/data/2.5/weather?id=2624652&appid=672e6780bb198824ed2d413b7c5244d2";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(MainActivity.ID, mIndex);
        editor.commit();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
        isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        AsyncTask mTask = new MyTask();
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
        dbHelper = new DBHelper(getApplicationContext());
        mIndex = sharedPreferences.getLong(MainActivity.ID,0);
        mTask.execute();
        isRunning = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(listener,filter);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
         return null;
    }

    private void doInBackgroundThing(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Model mModel = new Model();
                try {
                    Log.d("JSON", "Getting weather data");
                    mModel.setDescription(jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
                    mIndex++;
                    mModel.setId(mIndex);
                    mModel.setTemp((long)(Double.parseDouble(jsonObject.getJSONObject("main").getString("temp"))-273.15));
                    String date = parseDateTime();
                    Log.d("D/I: ", date + " : " + mIndex);
                    mModel.setTimestamp(parseDateTime());
                    Log.d("SQLservice", mModel.toString());
                    dbHelper.createWeather(mModel);
                    SharedPreferences sharedPref = getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putLong(MainActivity.ID, mIndex);
                    editor.apply();
                    sendLocalBroadcast();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        Singleton.getInstance(this).add(jsonObjectRequest);
    }
    private class MyTask extends AsyncTask<Object,Object,Object>{

        @Override
        protected Object doInBackground(Object... voids) {
            while(true){
                doInBackgroundThing();
                try{
                    Thread.sleep(30*60*1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    return null;
                }
            }
        }

    }
    private String parseDateTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
        );
        return format.format(date);
    }

    private void sendLocalBroadcast(){
        Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(MainActivity.RECIVE);
        Log.d("Service", "Recived net weather data");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }


    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doInBackgroundThing();
        }
    };
}
