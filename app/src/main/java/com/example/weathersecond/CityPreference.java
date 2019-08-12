package com.example.weathersecond;

import android.app.Activity;
import android.content.SharedPreferences;

class CityPreference {
    private SharedPreferences prefs;

    CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city", "Moscow,ru");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).apply();
    }
}
