package com.fubezz.fitnessapp.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.fubezz.fitnessapp.NewRunActivity;

/**
 * Created by fubezz on 06.06.16.
 */
public class StepDetectionListener implements SensorEventListener {

    private int steps = 0;
    private boolean currentlyRunning = false;
    private Sensor sensor;

    public StepDetectionListener(NewRunActivity newRunActivity){
        final SensorManager sensorManager = (SensorManager) newRunActivity.getSystemService(Context.SENSOR_SERVICE);
         sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(sensor != null){
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }else{
            Toast.makeText(newRunActivity, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            if (currentlyRunning){
                steps++;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean isCurrentlyRunning() {
        return currentlyRunning;
    }

    public void setCurrentlyRunning(boolean currentlyRunning) {
        this.currentlyRunning = currentlyRunning;
    }

    public Sensor getSensor() {
        return sensor;
    }
}
