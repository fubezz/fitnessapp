package com.fubezz.fitnessapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fubezz.fitnessapp.listener.LocationTrackerListener;
import com.fubezz.fitnessapp.listener.StepDetectionListener;
import com.fubezz.fitnessapp.model.DbHandler;
import com.fubezz.fitnessapp.model.RunSession;

import java.util.LinkedList;
import java.util.List;

public class NewRunActivity extends AppCompatActivity{

    private Handler timerHandler;
    private Handler stepHandler = null;
    private long startTime = 0L;
    private long currentTimer = 0L;
    private long startDate;
    private TextView stepCounter;
    private StepDetectionListener stepDetector = null;
    private List<Location> locList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_run);
        Toolbar toolbar = (Toolbar) findViewById(R.id.newRun_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Button startButton = (Button) findViewById(R.id.btnNewRunStart);
        final Button stopButton = (Button) findViewById(R.id.btnNewRunStop);
        stopButton.setEnabled(false);
        final Button resetButton = (Button) findViewById(R.id.btnNewRunReset);
        resetButton.setEnabled(false);
        final Button saveButton = (Button) findViewById(R.id.btnNewRunSave);
        saveButton.setEnabled(false);

        final TextView timerValue = (TextView) findViewById(R.id.timeTextView);
        timerHandler = new Handler();
        locList = new LinkedList<Location>();
        stepCounter = (TextView) findViewById(R.id.stepLabel);
        stepDetector = new StepDetectionListener(this);
        if (stepDetector.getSensor() != null){
            stepDetector.setCurrentlyRunning(false);
            stepDetector.setSteps(0);
            stepHandler = new Handler();
        }



        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final LocationTrackerListener myListener = new LocationTrackerListener(this, locList);



        if (startButton != null) {
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //resetButton.callOnClick();
                    stepDetector.setSteps(0);
                    timerValue.setText("00:00:00");
                    stepCounter.setText("Steps: 0");
                    locList.clear();
                    stopButton.setEnabled(true);
                    startButton.setEnabled(false);
                    startTime = SystemClock.uptimeMillis();
                    startDate = System.currentTimeMillis();
                    timerHandler.postDelayed(timeCounterThread, 0);
                    if (ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        String locationProvider = LocationManager.NETWORK_PROVIDER;
                        myListener.oldLocation = locationManager.getLastKnownLocation(locationProvider);
                        locationManager.requestLocationUpdates(myListener.getProviderName(), myListener.minTime, myListener.minDistance, myListener);
                    } else {
                        Toast.makeText(NewRunActivity.this,
                                "For GPS tracking please set permissions", Toast.LENGTH_SHORT)
                                .show();
                    }
                    if (stepHandler != null){
                        stepDetector.setCurrentlyRunning(true);
                        stepHandler.postDelayed(stepDetectorThread,0);
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

                    timerHandler.removeCallbacks(timeCounterThread);
                    if (ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(NewRunActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.removeUpdates(myListener);
//                        Toast.makeText(NewRunActivity.this,
//                                "Debug GPS positions: " + locList.size(), Toast.LENGTH_SHORT)
//                                .show();
                    }
                    if (stepHandler != null){
                        stepDetector.setCurrentlyRunning(false);
                        stepHandler.removeCallbacks(stepDetectorThread);
                    }


                }
            });
        }

        if (resetButton != null) {
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(NewRunActivity.this)
                            .setTitle("Reset run session")
                            .setMessage("Are you sure you want to reset the session?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startButton.setEnabled(true);
                                    resetButton.setEnabled(false);
                                    saveButton.setEnabled(false);
                                    stepDetector.setSteps(0);
                                    timerValue.setText("00:00:00");
                                    stepCounter.setText("Steps: 0");
                                    locList.clear();
                                    new DbHandler(NewRunActivity.this).deleteRunSession(new RunSession(startDate,currentTimer,"No Name",locList,stepDetector.getSteps()));
                                    Toast.makeText(NewRunActivity.this, "Reset Session", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final RunSession session = new RunSession(startDate,currentTimer,"No Name",locList,stepDetector.getSteps());
                    final EditText name = new EditText(NewRunActivity.this);
                    name.setText("No Name");
                    new AlertDialog.Builder(NewRunActivity.this)
                            .setTitle("Please type a name")
                            .setView(name)
                            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String n = name.getText().toString();
                                    session.setName(n);
                                    Log.v("SetNameForSession: ", session.getName());
                                    DbHandler saver = new DbHandler(NewRunActivity.this);
                                    saver.updateRunSession(session);
                                    saveButton.setEnabled(false);
                                    startButton.setEnabled(true);
                                    resetButton.setEnabled(false);
                                }
                            }).show();
                }
            });
        }
    }


    private Runnable timeCounterThread = new Runnable() {
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

            if (locList.size() >= 100){
                final RunSession session = new RunSession(startDate, currentTimer,"No Name",locList,stepDetector.getSteps());
                DbHandler saver = new DbHandler(NewRunActivity.this);
                saver.updateRunSession(session);
                locList.clear();
            }

            timerHandler.postDelayed(this,1000);
        }
    };

    private Runnable stepDetectorThread = new Runnable() {
        @Override
        public void run() {
            int steps = stepDetector.getSteps();
            stepCounter.setText("Steps: " + steps);
            stepHandler.postDelayed(this,10000);

        }
    };



}
