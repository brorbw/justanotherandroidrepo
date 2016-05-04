package com.larusaarhus.weatheraarhus7;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by brorbw on 04/05/16.
 */
public class SQLService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void doInBackgroundThing(){
        AsyncTask task = new ATask();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class ATask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            InputStream is = null;
            int len = 500;
            String s = "wrong";
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?id=2624652&appid=672e6780bb198824ed2d413b7c5244d2");
                Log.d("lols", "first");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                Log.d("LOLS","second");
                conn.setReadTimeout(1000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                Log.d("LOLS", "Getting rdy to connect");
                conn.connect();
                Log.d("LOLS", "IT CONNECTED");
                int response = conn.getResponseCode();
                Log.d("DEBUGGGG", "IT IS: " + response);
                is = conn.getInputStream();
                Log.d("LOL", "Got the inputString0");
                s = readIt(is, len);
                return null;


            } catch (Exception e) {
                //Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Log.d("LOLS", "SOMETGING WENT WRONG");

            } finally {
                if(is != null){
                    try {
                        is.close();
                    } catch (IOException e) {

                    }
                }
            }
            return null;
        }
    }

    private class SaveImage extends AsyncTask<Bitmap, Void, Void> {
        @Override
        protected Void doInBackground(Bitmap ... b) {
            if(getApplicationContext().getFilesDir().canWrite()) {
                String path = getApplicationContext().getFilesDir() + "/" + ".jpg";
                File myFile = new File(path);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(myFile);
                    b[0].compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (Exception e) {
                    Log.d("Camera", "unable to write to buffer");
                } finally {
                    //fos.close();
                }
            } else {
                Log.d("Camera","unable to write to media. No permissions");
                throw new NullPointerException();
            }
            return null;
        }
    }

    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);


    }
}
