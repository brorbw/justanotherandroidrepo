package com.larusaarhus.weatheraarhusgroup7;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brorbw on 05/05/16.
 */
public class DBHelper extends SQLiteOpenHelper{
    private static final String LOG = "DB_Helper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "weather";
    private static final String TABLE_NAME = "temps";
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEMP = "temp";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "("+ KEY_ID + " LONG PRIMARY KEY," + KEY_DESCRIPTION
            + " TEXT," + KEY_DATE + " DATETIME," + KEY_TEMP + " LONG)";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    public long createWeather(Model model){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, model.getId());
        values.put(KEY_DESCRIPTION, model.getDescription());
        values.put(KEY_TEMP, model.getTemp());
        values.put(KEY_DATE, model.getTimestamp());
        model.bigPrint();

        long weather_id = db.insert(TABLE_NAME, null, values);

        return weather_id;
    }
    public Model getModel(long model_id){
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +
                " = " + model_id;
        Log.d(LOG, selectQuery);
        return initiateModel(giveTheCurser(selectQuery));
    }
    public Model getModel(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREF,Context.MODE_PRIVATE);
        String model_id = ((Long)(sharedPreferences.getLong(MainActivity.ID,0))).toString();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID +
                " = " + model_id;
        Log.d(LOG, selectQuery);
        return initiateModel(giveTheCurser(selectQuery));
    }

    public List<Model> getAllModels(){
        List<Model> models = new ArrayList<Model>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor c = giveTheCurser(selectQuery);
        if(c.moveToLast()){
            for(int i = 0; i < 48; i++){
                if(c.moveToPrevious()){
                    models.add(initiateModel(c));
                }
            }
        }
        return models;
    }


    public Cursor giveTheCurser(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if(c != null){
            c.moveToFirst();
        } else {
            Log.wtf(LOG, "SOMETHING TERRIBLE HAPPEND");
        }
        return c;
    }

    public Model initiateModel(Cursor c){
        Model model = new Model();
        model.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        model.setDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));
        model.setTemp(c.getLong(c.getColumnIndex(KEY_TEMP)));
        model.setTimestamp(c.getString(c.getColumnIndex(KEY_DATE)));
        return model;
    }

    //deletes the old entries
    public void deleteModels(Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREF,Context.MODE_PRIVATE);
        long id = sharedPreferences.getLong(MainActivity.ID,0);
        if(id == 0){
            return;
        }
        String deleteQuery = "DELETE FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + (id-48);
        db.delete(TABLE_NAME, KEY_ID + " <= " + (id-48),null);
    }
}
