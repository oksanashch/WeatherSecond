package com.example.weathersecond;
import android.content.SharedPreferences;

class CityPreference {
    private SharedPreferences prefs;

    CityPreference(SharedPreferences prefs){
        this.prefs = prefs;
    }

    String getCity(){
        return prefs.getString("city", "Moscow,ru");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }

}
