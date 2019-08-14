package com.example.weathersecond;


import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import static android.content.Context.SENSOR_SERVICE;

public class SensorFragment extends Fragment {

    private TextView getTemp;
    private TextView getHumid;
    private SensorManager sensorManager;
    private Sensor sensorTemp;
    private Sensor sensorHumid;

    public SensorFragment() {
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        setHasOptionsMenu(true);
        initViews(v);
        getSensors();
        return v;
    }


    private void initViews(View v) {
        getTemp = v.findViewById(R.id.getTemp);
        getHumid = v.findViewById(R.id.getHumid);
    }

    @SuppressLint("SetTextI18n")
    private void getSensors() {
        sensorManager = (SensorManager)(Objects.requireNonNull(getActivity())).getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        sensorTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumid = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        if (sensorTemp == null) {
            getTemp.setText("no data");
        }
        if (sensorHumid == null) {
            getHumid.setText("no data");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensorTemp != null) {
            sensorManager.registerListener(listener, sensorTemp,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorHumid != null) {
            sensorManager.registerListener(listener, sensorHumid,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    private void showSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        Sensor sensor = event.sensor;
            if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                stringBuilder.append(event.values[0]).append(" °C");
                getTemp.setText(stringBuilder);
            } else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
                stringBuilder.append(event.values[0]).append(" %");
                getHumid.setText(stringBuilder);
            }

    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showSensors(event);
        }

    };

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item=menu.findItem(R.id.change_city);
        if(item!=null)
            item.setVisible(false);
    }

}
