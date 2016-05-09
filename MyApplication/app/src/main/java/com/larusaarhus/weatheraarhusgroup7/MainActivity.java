package com.larusaarhus.weatheraarhusgroup7;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
    private DBHelper dbHelper;
    private ModelAdapter adapter;


    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Do something with the database update ui
            adapter.setModels(getDatabaseHelper().getAllModels());
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
            upDateTextFeild();
        }
    };

    public void upDateTextFeild(){
        TextView textView = (TextView) findViewById(R.id.textmain);
        Model model = dbHelper.getModel(getApplicationContext());
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
        listView = (ListView) findViewById(R.id.list);
        adapter = new ModelAdapter(this,getDatabaseHelper().getAllModels());
        listView.setAdapter(adapter);
        Intent mIntent = new Intent(this, SQLService.class);


        if(!SQLService.isRunning) {
            Log.d("Main", "Is Running : " + SQLService.isRunning);
            startService(mIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECIVE);
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, filter);
        SharedPreferences shared = getSharedPreferences(PREF, Context.MODE_PRIVATE);
        boolean isFirst = shared.getBoolean(FIRST, true);
        if(!isFirst){
            upDateTextFeild();
        } else {
            SharedPreferences.Editor editor = shared.edit();
            editor.putBoolean(FIRST,false);
            editor.apply(); //Using apply because the data is not vital and and UI is more important
        }

    }

    private class ModelAdapter extends BaseAdapter {
        private List<Model> models;
        private Context context;

        Model model;

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
        public List<Model> getModels() {
            return models;
        }

        public void setModels(List<Model> tasks) {
            this.models = tasks;
        }
    }
    public DBHelper getDatabaseHelper(){
        if(dbHelper == null){
            dbHelper = new DBHelper(getApplicationContext());
        }
        return dbHelper;
    }

    public void update(View view){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.UPDATE);
        Log.d("Service", "Recived net weather data");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
