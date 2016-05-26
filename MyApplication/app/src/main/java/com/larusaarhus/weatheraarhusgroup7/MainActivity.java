package com.larusaarhus.weatheraarhusgroup7;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.larusaarhus.weatheraarhus7.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREF = "com.larusaarhus.weather7.PREF";
    public static final String ID = "com.larusaarhus.weather7.ID";
    public static final String RECIVE = "com.larusaarhus.weather7.RECIVE";
    public static final String UPDATE = "com.larusaarhus.weather7.UPDATE";
    public static final String FIRST = "com.larusaarhus.weather7.FIRST";
    private ListView listView;
    private ModelAdapter adapter;
    private List<Model> past;
    private Model model;
    private boolean isAdapter = false;
    private boolean isItFirst = false;
    SQLService mService;
    boolean mBound = false;


    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Do something with the database update ui
            if(!isItFirst){
                isItFirst = true;
                SharedPreferences shared = getSharedPreferences(PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putBoolean(FIRST,isItFirst);
                editor.commit();
            }
            updateUi();
        }
    };

    public void updateUi(){
        if(mBound){
            past = mService.getPastWeather();

                adapter.setModels(past);
                ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                upDateTextFeild();
        }

    }

    public void upDateTextFeild(){
        TextView textView = (TextView) findViewById(R.id.textmain);
        model = mService.getCurrentWeather();
        String temp = ((Double)model.getTemp()).toString();
        String description = model.getDescription();
        if(temp != null && description != null) {
            textView.setText(description + "\n" + temp + "°C");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mIntent = new Intent(this, SQLService.class);
        if(!SQLService.isRunning) {
            Log.d("Main", "Is Running : " + SQLService.isRunning);
            startService(mIntent);
        }
        listView = (ListView) findViewById(R.id.list);
        adapter = new ModelAdapter(this,past);
        isAdapter = true;
        listView.setAdapter(adapter);
        updateUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SQLService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

            if(mBound){
                past = mService.getPastWeather();
            }
            adapter.setModels(past);
           ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
             updateUi();

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }








    @Override
    protected void onPause() {
        super.onPause();

    }
    public int test = 0;
    @Override
    protected void onResume() {
        super.onResume();


    }

    private class ModelAdapter extends BaseAdapter {
        private List<Model> models;
        private Context context;


        public ModelAdapter(Context context, List<Model> models){
            this.context = context;
            this.models = models;
        }

        @Override
        public int getCount() {
            if(models == null){
                return 0;
            }
            return models.size();
        }

        @Override
        public Object getItem(int i) {
            if(models != null && models.size() > i){
                return models.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater;
                inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.list_item, null);
            }
            if(models != null && models.size()>i){
                Model tmp = models.get(i);
                TextView des = (TextView) view.findViewById(R.id.des);
                TextView temp = (TextView) view.findViewById(R.id.tmp);
                TextView date = (TextView) view.findViewById(R.id.date);
                des.setText(tmp.getDescription());
                temp.setText(String.valueOf(tmp.getTemp())+ "°C");
                date.setText(tmp.getTimestamp());
                return view;
            }
            return null;
        }

        public void setModels(List<Model> tasks) {
            this.models = tasks;
        }
    }

    public void update(View view){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.UPDATE);
        Log.d("Service", "Recived net weather data");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SQLService.LocalBinder binder = (SQLService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d("mCOnnection", "" + mBound);
            IntentFilter filter = new IntentFilter();
            filter.addAction(RECIVE);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(listener, filter);
            SharedPreferences shared = getSharedPreferences(PREF, Context.MODE_PRIVATE);
            isItFirst = shared.getBoolean(FIRST, false);


            if(isItFirst){
                updateUi();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(listener);
            mBound = false;
        }

    };


}
