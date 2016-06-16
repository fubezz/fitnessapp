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
    private String name;
    private String date;
    private String time;
    private String locations;
    private String times;
    private int steps;


    public RunSession(long currentTimer, String name, List<Location> locList, int steps) {

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy, HH:mm");
        this.date = df.format(Calendar.getInstance().getTime());
        Log.v("Date", date);
        this.dateLong = Calendar.getInstance().getTimeInMillis();
        this.name = name;
        this.time = Long.toString(currentTimer);
        Log.v("time", time);

        this.locations = "";

        //Format: <long/lat/time/acc/speed>
        for(Location l : locList){
            String curr = "<";
            curr += Double.toString(l.getLongitude()) + "/";
            curr += Double.toString(l.getLatitude()) + "/";
            curr += Long.toString(l.getTime()) + "/";
            if(l.hasAccuracy()){
                curr += Float.toString(l.getAccuracy()) + "/";
            }
            if(l.hasSpeed()){
                curr += Float.toString(l.getSpeed());
            }
            curr += ">";
            this.locations += curr;
        }
        Log.v("Locations", locations);

        this.steps = steps;


    }
    public RunSession(long dateLong,String name ,String date, String time,String locations, int steps) {
        this.dateLong = dateLong;
        this.name = name;
        this.date = date;
        this.time = time;
        this.locations = locations;
        this.steps = steps;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
