package com.fubezz.fitnessapp.listener;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by fubezz on 02.06.16.
 */
public class LocationTrackerListener implements LocationListener {

    private AppCompatActivity activity;
    private List<Location> locList;

    public static long minTime = 5 * 1000; // Minimum time interval for update in milliseconds, i.e. 5 seconds.
    public static long minDistance = 5; // Minimum distance change for update in meters, i.e. 10 meters.
    public Location oldLocation;


    public LocationTrackerListener(AppCompatActivity activity, List<Location> locList) {
        this.activity = activity;
        oldLocation = null;
        this.locList = locList;


    }

    /**
     * Get provider name.
     * @return Name of best suiting provider.
     * */
    public String getProviderName() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(true); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        Toast.makeText(activity, "Using Provider: " + locationManager.getBestProvider(criteria, true),Toast.LENGTH_SHORT).show();
        return locationManager.getBestProvider(criteria, true);
    }

    /**
     * Make use of location after deciding if it is better than previous one.
     *
     * @param location Newly acquired location.
     */
    void doWorkWithNewLocation(Location location) {
        if (isBetterLocation(oldLocation, location)) {
            locList.add(location);
            Log.v("Location:", location.toString());
            oldLocation = location;
        }

    }

    /**
     * Time difference threshold set for one minute.
     */
    static final int TIME_DIFFERENCE_THRESHOLD = 1 * 30 * 1000; // in ms
    static final int ACCURACY_DIFFERENCE_THRESHOLD = 10; //in m
    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best location strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() <= oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            // If time difference is not greater then allowed threshold we accept it.
            if (timeDifference > TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }else if (!isMoreAccurate && isNewer){
            float accDifference = newLocation.getAccuracy() - oldLocation.getAccuracy();
            if (accDifference <= ACCURACY_DIFFERENCE_THRESHOLD){
                return true;
            }
        }

        return false;
    }


    @Override
    public void onLocationChanged(Location location) {
        // Do work with new location. Implementation of this method will be covered later.
        doWorkWithNewLocation(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(activity,
                "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                .show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(activity,
                "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                .show();
    }



}
