package com.fubezz.fitnessapp.model;

import android.location.Location;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.List;

/**
 * Created by fubezz on 02.06.16.
 */
public class RunSession {



    private long dateLong;
    private String date;
    private String time;
    private String locations;


    public RunSession(long currentTimer, List<Location> locList) {

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy, HH:mm");
        date = df.format(Calendar.getInstance().getTime());
        Log.v("Date", date);
        dateLong = Calendar.getInstance().getTimeInMillis();

        time = Long.toString(currentTimer);
        Log.v("time", time);

        locations = "";

        //Format: <long/lat/acc/speed>
        for(Location l : locList){
            String curr = "<";
            curr += Double.toString(l.getLongitude()) + "/";
            curr += Double.toString(l.getLatitude()) + "/";
            if(l.hasAccuracy()){
                curr += Float.toString(l.getAccuracy()) + "/";
            }
            if(l.hasSpeed()){
                curr += Float.toString(l.getSpeed());
            }
            curr += ">";
            locations += curr;
        }
        Log.v("Locations", locations);


    }
    public RunSession(long dateLong,String date, String time,String locations) {
        this.dateLong = dateLong;
        this.date = date;
        this.time = time;
        this.locations = locations;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public String toString(){
        return this.getDate();
    }

}
