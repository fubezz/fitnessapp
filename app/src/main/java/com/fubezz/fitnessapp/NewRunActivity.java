package com.fubezz.fitnessapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fubezz.fitnessapp.listener.LocationTrackerListener;
import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;

import java.util.LinkedList;
import java.util.List;

public class NewRunActivity extends AppCompatActivity {

    private Handler timerHandler;
    private long startTime = 0L;
    private long currentTimer = 0L;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button startButton = (Button) findViewById(R.id.btnNewRunStart);
        final Button stopButton = (Button) findViewById(R.id.btnNewRunStop);
        stopButton.setEnabled(false);
        final Button resetButton = (Button) findViewById(R.id.btnNewRunReset);
        resetButton.setEnabled(false);
        final Button saveButton = (Button) findViewById(R.id.btnNewRunSave);
        saveButton.setEnabled(false);
        final TextView timerValue = (TextView) findViewById(R.id.timeTextView);

        timerHandler = new Handler();


        final List<Location> locList = new LinkedList<Location>();
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationTrackerListener myListener = new LocationTrackerListener(this, locList);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        myListener.oldLocation = locationManager.getLastKnownLocation(locationProvider);

        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetButton.callOnClick();
                    stopButton.setEnabled(true);
                    startButton.setEnabled(false);
                    startTime = SystemClock.uptimeMillis();
                    timerHandler.postDelayed(updateTimerThread, 0);
                    if (ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(myListener.getProviderName(), myListener.minTime, myListener.minDistance, myListener);
                    } else {
                        Toast.makeText(NewRunActivity.this,
                                "For GPS tracking please set permissions", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });
        }

        if (stopButton != null) {
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetButton.setEnabled(true);
                    saveButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    timerHandler.removeCallbacks(updateTimerThread);
                    if (ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeUpdates(myListener);
                        Toast.makeText(NewRunActivity.this,
                                "Debug GPS positions: " + locList.size(), Toast.LENGTH_SHORT)
                                .show();
                    }

                }
            });
        }

        if (resetButton != null) {
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startButton.setEnabled(true);
                    resetButton.setEnabled(false);
                    saveButton.setEnabled(false);
                    startTime = 0;
                    timerValue.setText("00:00:00");
                    locList.clear();
                }
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RunSession session = new RunSession(currentTimer,locList);
                    DbHandler saver = new DbHandler(NewRunActivity.this);
                    saver.addRunSession(session);
                    saveButton.setEnabled(false);
                    startButton.setEnabled(true);
                    resetButton.setEnabled(false);
                }
            });
        }
    }


    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            currentTimer = timeInMilliseconds;
            int secs = (int) timeInMilliseconds / 1000;
            int hours = secs / 3600;
            secs = secs % 3600;
            int mins = secs / 60;
            secs = secs % 60;
            TextView timerValue = (TextView) findViewById(R.id.timeTextView);
            timerValue.setText("" + hours + ":" + String.format("%02d",mins) + ":" + String.format("%02d",secs));
            timerHandler.postDelayed(this,0);
        }
    };



}
