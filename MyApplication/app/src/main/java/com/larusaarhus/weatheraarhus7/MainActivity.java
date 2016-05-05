package com.larusaarhus.weatheraarhus7;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String PREF = "com.larusaarhus.weather7.PREF";
    public static final String ID = "com.larusaarhus.weather7.ID";
    public static final String ACTION = "com.larusaarhus.weather7.ACTION";
    private ListView listView;
    private DBHelper dbHelper;
    private ModelAdapter adapter;


    private BroadcastReceiver listener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Do something with the database update ui
            adapter.setModels(getDatabaseHelper().getAllModels());
            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        adapter = new ModelAdapter(this,getDatabaseHelper().getAllModels());
        listView.setAdapter(adapter);
        Intent mIntent = new Intent(this, SQLService.class);
        startService(mIntent);
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
        filter.addAction(ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(listener, filter);
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
                temp.setText(String.valueOf(tmp.getTemp()));
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
}
