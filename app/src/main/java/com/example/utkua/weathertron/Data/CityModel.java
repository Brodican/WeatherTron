package com.example.utkua.weathertron.Data;

import android.util.Log;

/**
 * Created by utkua on 8/9/2017.
 */

public class CityModel {
    private String name;
    private String temp;
    private String time;

    private static final String TAG = "CityModel";

    public String getTime() {
        Log.d(TAG, "time in getTime:" + time);
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public CityModel(String inName, String inTime, String inTemp) {
        name = inName;
        time = inTime;
        temp = inTemp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
