package com.example.utkua.weathertron.Data;

/**
 * Created by utkua on 8/7/2017.
 */

public class WeatherModel {
    private  String dayInfo;
    private  int minTempInfo;
    private  int maxTempInfo;
    private  int imgId;

    public String getDayInfo() {
        return dayInfo;
    }

    public void setDayInfo(String dayInfo) {
        this.dayInfo = dayInfo;
    }

    public int getMinTempInfo() {
        return minTempInfo;
    }

    public void setMinTempInfo(int minTempInfo) {
        this.minTempInfo = minTempInfo;
    }

    public int getMaxTempInfo() {
        return maxTempInfo;
    }

    public void setMaxTempInfo(int maxTempInfo) {
        this.maxTempInfo = maxTempInfo;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
