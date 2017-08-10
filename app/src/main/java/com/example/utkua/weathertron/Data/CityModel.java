package com.example.utkua.weathertron.Data;

/**
 * Created by utkua on 8/9/2017.
 */

public class CityModel {
    private String name;
    private String temp;

    public CityModel(String inName) {
        name = inName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
