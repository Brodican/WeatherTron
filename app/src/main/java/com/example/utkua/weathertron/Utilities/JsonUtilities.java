package com.example.utkua.weathertron.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by utkua on 8/7/2017.
 */

public class JsonUtilities {
    public static String[] getCurrentFromJson(String forecastJsonStr)
            throws JSONException {
        // jsonObject containing all data, unformatted
        JSONObject jsonObject = new JSONObject(forecastJsonStr);
        // "main" gets main details from the sent JSONObject
        JSONObject weatherMain = new JSONObject(jsonObject.getString("main"));
        // JSONArray of weather
        JSONArray weatherWeath = jsonObject.getJSONArray("weather");
        // Returns temperature in degrees Kelvin
        Double tempK = Double.parseDouble(weatherMain.getString("temp"));
        Double tempMaxK = Double.parseDouble(weatherMain.getString("temp_max"));
        Double tempMinK = Double.parseDouble(weatherMain.getString("temp_min"));
        // Cast to int to remove decimal
        int tempC = (int) (tempK - 273);
        int tempMaxC = (int) (tempMaxK - 273);
        int tempMinC = (int) (tempMinK - 273);
        // Gets name
        String locationName = jsonObject.getString("name");
        // Split JSONArray into sections
        JSONObject weatherObj = weatherWeath.getJSONObject(0);
        String weatherDescription = weatherObj.getString("description");
        // Declare String array to hold data, place data in array
        String[] allData = new String[5];
        // Put temp, name, and weather description in array to be sent
        allData[0] = Integer.toString(tempC) + "°";
        allData[1] = Integer.toString(tempMinC);
        allData[2] = Integer.toString(tempMaxC);
        allData[3] = locationName;
        allData[4] = weatherDescription;

        return allData;
    }

    public static String[] get7DaysFromJson (String forecastJsonStr)
            throws JSONException {
        // jsonObject containing all data, unformatted
        JSONObject jsonObject = new JSONObject(forecastJsonStr);
        JSONArray days = jsonObject.getJSONArray("list");
        // Declare String array to hold data, place data in array
        String[] allData = new String[24];

        for (int i = 0; i < 8; i++) {
            JSONObject day = days.getJSONObject(i+1);
            JSONObject tempInfo = day.getJSONObject("temp");
            Double maxTempN = Double.parseDouble(tempInfo.getString("max"));
            int maxTemp = (int) (maxTempN - 273);
            allData[i] = String.valueOf(maxTemp);
        }
        for (int i = 8; i < 16; i++) {
            JSONObject day = days.getJSONObject(i-7);
            JSONObject tempInfo = day.getJSONObject("temp");
            Double minTempN = Double.valueOf(tempInfo.getString("min"));
            int minTemp = (int) (minTempN - 273);
            allData[i] = String.valueOf(minTemp);
        }
        for (int i = 16; i < 24; i++) {
            JSONObject day = days.getJSONObject(i-15);
            JSONArray weather = day.getJSONArray("weather");
            JSONObject weatherInfo = weather.getJSONObject(0);
            String imageID = weatherInfo.getString("id");
            allData[i] = imageID;
        }
        return allData;
    }

    public static String[] getAllDayFromJson (String forecastJsonStr)
            throws JSONException {
        // jsonObject containing all data, unformatted
        JSONObject jsonObject = new JSONObject(forecastJsonStr);
        JSONArray days = jsonObject.getJSONArray("list");
        // Declare String array to hold data, place data in array
        String[] allData = new String[24];

        for (int i = 0; i < 8; i++) {
            JSONObject day = days.getJSONObject(i+1);
            JSONObject tempInfo = day.getJSONObject("temp");
            Double maxTempN = Double.parseDouble(tempInfo.getString("max"));
            int maxTemp = (int) (maxTempN - 273);
            allData[i] = String.valueOf(maxTemp);
        }
        for (int i = 8; i < 16; i++) {
            JSONObject day = days.getJSONObject(i-7);
            JSONObject tempInfo = day.getJSONObject("temp");
            Double minTempN = Double.valueOf(tempInfo.getString("min"));
            int minTemp = (int) (minTempN - 273);
            allData[i] = String.valueOf(minTemp);
        }
        for (int i = 16; i < 24; i++) {
            JSONObject day = days.getJSONObject(i-15);
            JSONArray weather = day.getJSONArray("weather");
            JSONObject weatherInfo = weather.getJSONObject(0);
            String imageID = weatherInfo.getString("id");
            allData[i] = imageID;
        }
        return allData;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
