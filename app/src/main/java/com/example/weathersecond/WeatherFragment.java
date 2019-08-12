package com.example.weathersecond;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weathersecond.WeatherData.ErrorResponseWeather;
import com.example.weathersecond.WeatherData.WeatherRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.String.format;

public class WeatherFragment extends Fragment {
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=ca0643207bc5ac6da08b6bf2cc93e560";


    private TextView cityField;
    private TextView updatedField;
    private TextView pressureField;
    private TextView humidityField;
    private TextView currentTemperatureField;
    private ImageView weatherIcon;

    Handler handler;

    public WeatherFragment() {
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = rootView.findViewById(R.id.city_field);
        updatedField = rootView.findViewById(R.id.updated_field);
        pressureField = rootView.findViewById(R.id.pressure_field);
        humidityField = rootView.findViewById(R.id.humidity_field);
        currentTemperatureField = rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = rootView.findViewById(R.id.weather_icon);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateWeatherData(new CityPreference(Objects.requireNonNull(getActivity())).getCity());
    }

    private void updateWeatherData(final String city) {

        try {
            final URL url = new URL(format(WEATHER_URL, city));
            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpsURLConnection urlConnection;
                        urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setReadTimeout(10000);
                        if (urlConnection.getResponseCode() != 200) {
                            getErrorWeatherRespond(urlConnection);
                        } else {
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String result = getLines(in);
                            Gson gson = new Gson();
                            final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                            handler.post(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    cityField.setText(weatherRequest.getName());
                                    @SuppressLint("DefaultLocale") String temp = format("%.0f C°",
                                            (double) weatherRequest.getMain().getTemp());
                                    currentTemperatureField.setText(temp);
                                    @SuppressLint("DefaultLocale") String press = format("%.0f hPa",
                                            (double) weatherRequest.getMain().getPressure());
                                    pressureField.setText(press);
                                    @SuppressLint("DefaultLocale") String humid = format("%.0f",
                                            (double) weatherRequest.getMain().getHumidity());
                                    humidityField.setText(humid + "%");
                                    int actualId = weatherRequest.getWeather()[0].getId();
                                    long sunrise = weatherRequest.getSys().getSunrise() * 1000;
                                    long sunset = weatherRequest.getSys().getSunset() * 1000;
                                    setWeatherIcon(actualId, sunrise, sunset);
                                    DateFormat df = DateFormat.getDateTimeInstance();
                                    String updatedOn = df.format(new Date((weatherRequest.getDt() * 1000)));
                                    updatedField.setText("Last update: " + updatedOn);
                                }
                            });
                        }

                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void getErrorWeatherRespond(HttpsURLConnection con) {
        BufferedReader inErr = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String resultErr = getLines(inErr);
        Gson gson = new Gson();
        final ErrorResponseWeather errorWeather = gson.fromJson(resultErr, ErrorResponseWeather.class);
        handler.post(new Runnable() {
            public void run() {
                String msg = errorWeather.getMessage();
                if (msg != null) {
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private String getLines(BufferedReader in) {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        try {
            String line;
            boolean flag = false;
            while ((line = in.readLine()) != null) {
                result.append(flag ? newLine : "").append(line);
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();

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

    public void changeCity(String city) {
        updateWeatherData(city);
    }


}
