package com.fubezz.fitnessapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by fubezz on 02.06.16.
 */
public class DbHandler extends SQLiteOpenHelper {

    List<Location> list;
    long timeInMillis = 0;

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SportSessions";

    // Contacts table name
    private static final String TABLE_RUN = "RunSessions";

    // Contacts Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_LOCATIONS = "locations";
    private static final String KEY_STEPS = "steps";



    public DbHandler(Context context) {
        //CHANGE FOR SAVING
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RUN_TABLE = "CREATE TABLE " + TABLE_RUN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_LOCATIONS + " TEXT," + KEY_STEPS + " INTEGER" + ")";
        Log.v("Table: ", CREATE_RUN_TABLE);
        db.execSQL(CREATE_RUN_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RUN);

        // Create tables again
        onCreate(db);
    }


    public void addRunSession(RunSession s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID,s.getDateLong());
        values.put(KEY_DATE, s.getDate());
        values.put(KEY_TIME, s.getTime());
        values.put(KEY_LOCATIONS,s.getLocations());
        values.put(KEY_STEPS,s.getSteps());
        // Inserting Row
        db.insert(TABLE_RUN, null, values);
        db.close(); // Closing database connection
    }

    public RunSession getRunSession(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RUN, new String[] { KEY_ID,
                        KEY_DATE, KEY_TIME, KEY_LOCATIONS, KEY_STEPS}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

       RunSession session = new RunSession(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));
        // return contact
        db.close();
        return session;
    }


    public ArrayList<RunSession> getAllRunSessions() {
        ArrayList<RunSession> sessionList = new ArrayList<RunSession>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_RUN;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RunSession contact = new RunSession(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));

                // Adding contact to list
                sessionList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        db.close();
        return sessionList;
    }


    public void deleteRunSession(RunSession session) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RUN, KEY_ID + "=?",
                new String[] { String.valueOf(session.getDateLong()) });
        db.close();
    }


}
