package com.larusaarhus.weatheraarhus7;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final String PREF = "com.larusaarhus.weather7.PREF";
    public static final String ID = "com.larusaarhus.weather7.ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent mIntent = new Intent(this, SQLService.class);
        startService(mIntent);
    }

}
