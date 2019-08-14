package com.example.weathersecond;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Date;
import java.util.Objects;

public class WeatherFragment extends Fragment {
    final static String BROADCAST_ACTION = "android_2.lesson04.app01.service_finished";

    private ServiceFinishedReceiver receiver = new ServiceFinishedReceiver();


    private TextView cityField;
    private TextView updatedField;
    private TextView pressureField;
    private TextView humidityField;
    private TextView currentTemperatureField;
    private ImageView weatherIcon;

    public WeatherFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        pressureField = rootView.findViewById(R.id.pressure_field);
        humidityField = rootView.findViewById(R.id.humidity_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
    }

        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
            Intent intent = new Intent(getActivity(), WeatherDataService.class);
            Objects.requireNonNull(getActivity()).startService(intent);
    }

    private class ServiceFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    String respCode = intent.getStringExtra(WeatherDataService.RESPOND_CODE);
                    if (respCode == null) {
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_LONG).show();
                    } else {
                        if (respCode.equals("200")) {
                            String cityName = intent.getStringExtra(WeatherDataService.CITYNAME);
                            cityField.setText(cityName);
                            String temp = intent.getStringExtra(WeatherDataService.TEMP);
                            currentTemperatureField.setText(temp);
                            String press = intent.getStringExtra(WeatherDataService.PRESS);
                            pressureField.setText(press);
                            String humid = intent.getStringExtra(WeatherDataService.HUMID);
                            humidityField.setText(humid + "%");
                            int actualId = Integer.valueOf(intent.getStringExtra(WeatherDataService.ACTID));
                            long sunrise = Long.valueOf(intent.getStringExtra(WeatherDataService.SUNRISE));
                            long sunset = Long.valueOf(intent.getStringExtra(WeatherDataService.SUNSET));
                            setWeatherIcon(actualId, sunrise, sunset);
                            String updateOn = intent.getStringExtra(WeatherDataService.UPDATE_ON);
                            updatedField.setText("Last update: " + updateOn);
                        } else {
                            String msg = intent.getStringExtra(WeatherDataService.ERRORMSG);
                            if (msg != null) {
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getActivity()).registerReceiver(receiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    public void onStop() {
        Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
        super.onStop();

    }



    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                weatherIcon.setImageResource(R.drawable.sun);
            } else {
                weatherIcon.setImageResource(R.drawable.sun_night);
            }
        } else {
            switch (id) {
                case 2:
                    weatherIcon.setImageResource(R.drawable.rainthunder);
                    break;
                case 3:
                    weatherIcon.setImageResource(R.drawable.sleet);
                    break;
                case 7:
                    weatherIcon.setImageResource(R.drawable.fog);
                    break;
                case 8:
                    weatherIcon.setImageResource(R.drawable.cloud);
                    break;
                case 6:
                    weatherIcon.setImageResource(R.drawable.snow);
                    break;
                case 5:
                    weatherIcon.setImageResource(R.drawable.rain);
                    break;
            }
        }
    }

    void changeCity() {
        Intent intent = new Intent(getActivity(), WeatherDataService.class);
        Objects.requireNonNull(getActivity()).startService(intent);
    }


}
