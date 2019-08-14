package com.example.weathersecond;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

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

import javax.net.ssl.HttpsURLConnection;

import static java.lang.String.format;


public class WeatherDataService extends IntentService {
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&appid=ca0643207bc5ac6da08b6bf2cc93e560";
    public static final String TEMP = "temp";
    public static final String CITYNAME ="cityName" ;
    public static final String PRESS = "press";
    public static final String HUMID = "humid";
    public static final String ACTID = "actualId" ;
    public static final String SUNRISE = "sunrise" ;
    public static final String SUNSET = "sunset";
    public static final String ERRORMSG = "Error";
    public static final String UPDATE_ON = "updateOn";
    public static final String RESPOND_CODE = "respondCode";
    private String cityName;
    private String temp;
    private String press;
    private String humid;
    private String updatedOn;
    private String actualId;
    private long sunrise;
    private long sunset;


    public WeatherDataService() {
        super("background_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent broadcastIntent = new Intent(WeatherFragment.BROADCAST_ACTION);
        CityPreference mySharedPreferences = new CityPreference(getApplicationContext().getSharedPreferences("city", Activity.MODE_PRIVATE));
        String city = mySharedPreferences.getCity();
        try {
            final URL url = new URL(format(WEATHER_URL, city));
            HttpsURLConnection urlConnection;
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            int respondCode = urlConnection.getResponseCode();
            broadcastIntent.putExtra(RESPOND_CODE, String.valueOf(respondCode));
            if (respondCode != 200) {
                getErrorWeatherRespond(urlConnection,broadcastIntent);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String result = getLines(in);
                Gson gson = new Gson();
                final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                getStringWeatherData(weatherRequest);
                setStringWeatherData(broadcastIntent);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendBroadcast(broadcastIntent);
    }

    @SuppressLint("DefaultLocale")
    private void getStringWeatherData(WeatherRequest weatherRequest) {
        cityName = weatherRequest.getName();
        temp = format("%.0f C°", (double) weatherRequest.getMain().getTemp());
        press = format("%.0f hPa", (double) weatherRequest.getMain().getPressure());
        humid = format("%.0f", (double) weatherRequest.getMain().getHumidity());
        actualId = String.valueOf(weatherRequest.getWeather()[0].getId());
        sunrise = weatherRequest.getSys().getSunrise() * 1000;
        sunset = weatherRequest.getSys().getSunset() * 1000;
        DateFormat df = DateFormat.getDateTimeInstance();
        updatedOn = df.format(new Date((weatherRequest.getDt() * 1000)));

    }

    private void setStringWeatherData(Intent intent){
        intent.putExtra(CITYNAME, cityName);
        intent.putExtra(TEMP, temp);
        intent.putExtra(PRESS, press);
        intent.putExtra(HUMID, humid);
        intent.putExtra(ACTID, actualId);
        intent.putExtra(SUNRISE, String.valueOf(sunrise));
        intent.putExtra(SUNSET, String.valueOf(sunset));
        intent.putExtra(UPDATE_ON, updatedOn);
    }

    private void getErrorWeatherRespond(HttpsURLConnection con, Intent intent) {
        BufferedReader inErr = new BufferedReader(new InputStreamReader(con.getErrorStream()));
        String resultErr = getLines(inErr);
        Gson gson = new Gson();
        final ErrorResponseWeather errorWeather = gson.fromJson(resultErr, ErrorResponseWeather.class);
        String msg = errorWeather.getMessage();
        if (msg != null) {
            intent.putExtra(ERRORMSG, msg);
        }
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
}


