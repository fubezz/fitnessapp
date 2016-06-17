package com.fubezz.fitnessapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "SportSessions";

    // Contacts table name
    private static final String TABLE_RUN = "RunSessions";

    // Contacts Table Columns names
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_DATE = "DATE";
    private static final String KEY_TIME = "TIME";
    private static final String KEY_LOCATIONS = "LOCATIONS";
    private static final String KEY_DISTANCE = "DISTANCE";
    private static final String KEY_STEPS = "STEPS";



    public DbHandler(Context context) {
        //CHANGE FOR SAVING
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RUN_TABLE = "CREATE TABLE " + TABLE_RUN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATE + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_LOCATIONS + " TEXT," + KEY_DISTANCE + " INTEGER," + KEY_STEPS + " INTEGER" + ")";
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
        values.put(KEY_NAME,s.getName());
        values.put(KEY_DATE, s.getDate());
        values.put(KEY_TIME, s.getTime());
        values.put(KEY_LOCATIONS,s.getLocations());
        values.put(KEY_DISTANCE,s.getDistance());
        values.put(KEY_STEPS,s.getSteps());
        // Inserting Row
        db.insert(TABLE_RUN, null, values);
        db.close(); // Closing database connection
    }

    public RunSession getRunSession(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RUN, new String[] { KEY_ID, KEY_NAME,
                        KEY_DATE, KEY_TIME, KEY_LOCATIONS, KEY_DISTANCE, KEY_STEPS}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

       RunSession session = new RunSession(cursor.getLong(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getInt(6));
       Log.v("Loaded Session: ", session.getDate() + ", " + session.getName());
        // return contact
        cursor.close();
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
                RunSession session = new RunSession(cursor.getLong(0), cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getInt(5),cursor.getInt(6));

                // Adding contact to list
                sessionList.add(session);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
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
