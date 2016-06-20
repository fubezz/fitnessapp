package com.fubezz.fitnessapp.model;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fubezz on 02.06.16.
 */
public class RunSession {

    private long id;
    private String name;
    private String date;
    private String time;
    //Format: <long/lat/time/acc/speed>
    private String locations;
    private int distance;
    private int steps;


    public RunSession(long id, long currentTimer, String name, List<Location> locList, int steps) {

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy, HH:mm");
        Date d = new Date();
        d.setTime(id);
        this.date = df.format(d);
        Log.v("Date", date);
        this.id = id;
        this.name = name;
        this.time = Long.toString(currentTimer);
        Log.v("time", time);

        this.locations = "";


        for(int i = 0; i < locList.size();i++){
            Location l = locList.get(i);
            if (i > 0) distance += (int) l.distanceTo(locList.get(i-1));
            String curr = "<";
            curr += Double.toString(l.getLongitude()) + "/";
            curr += Double.toString(l.getLatitude()) + "/";
            curr += Long.toString(l.getTime()) + "/";
            if(l.hasAltitude()){
                curr += Double.toString(l.getAltitude()) + "/";
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
    public RunSession(long id, String name , String date, String time, String locations, int distance, int steps) {
        //date in milliseconds
        this.id = id;
        this.name = name;
        this.date = date;
        //time in milliseconds
        this.time = time;
        this.locations = locations;
        //distance in m
        this.distance = distance;
        this.steps = steps;
    }

    public List<Location> getListofLocations(){
        List<Location> resList = new ArrayList<>();
        if(locations != null && locations.length() > 0){
            String[] loc = locations.substring(1, locations.length()-1).split("><");
            if(loc.length > 1) {
                for (String l : loc) {
                    String[] pos = l.split("/");
                    double latitude = Double.parseDouble(pos[1]);
                    double longitude = Double.parseDouble(pos[0]);
                    LatLng p = new LatLng(latitude, longitude);
                    Location lo = new Location("");
                    lo.setLatitude(latitude);
                    lo.setLongitude(longitude);
                    lo.setTime(Long.parseLong(pos[2]));
                    if (pos.length > 3) lo.setAltitude(Double.parseDouble(pos[3]));
                    else lo.setAltitude(Double.MIN_VALUE);
                    if (pos.length > 4) lo.setSpeed(Float.parseFloat(pos[4]));
                    else lo.setSpeed(Float.MIN_VALUE);
                    resList.add(lo);

                }
            }
            return resList;
        }
        return null;
    }

    public void computeDistance(){
        List<Location> locList = getListofLocations();
        int distance = 0;
        for (int i = 0; i < locList.size();i++){
            if (i > 0) distance += locList.get(i).distanceTo(locList.get(i-1));
        }
        setDistance(distance);
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
