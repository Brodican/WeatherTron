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
        // Cast to int to remove decimal
        int tempC = (int) (tempK - 273);
        // Gets name
        String locationName = jsonObject.getString("name");
        // Split JSONArray into sections
        String[] weatherArr = weatherWeath.getString(0).split(",");
        // Get information from sections
        String[] weatherDescriptionArr = weatherArr[2].split(":");
        // Get weather description from second index of split
        String weatherDescription = weatherDescriptionArr[1];
        // Declare String array to hold data, place data in array
        String[] allData = new String[3];
        // Put temp, name, and weather description in array to be sent
        allData[0] = Integer.toString(tempC) + "℃";
        allData[1] = locationName;
        allData[2] = weatherDescription;

        return allData;
    }

    public static String[] get7DaysFromJson (String forecastJsonStr)
            throws JSONException {
        // jsonObject containing all data, unformatted
        JSONObject jsonObject = new JSONObject(forecastJsonStr);
        JSONArray days = jsonObject.getJSONArray("list");
        // Declare String array to hold data, place data in array
        String[] allData = new String[14];

        for (int i = 0; i < 7; i++) {
            JSONObject day = days.getJSONObject(i);
            JSONObject tempInfo = day.getJSONObject("temp");
            Double dayTempN = Double.valueOf(tempInfo.getString("day")) - 273;
            Double dayTemp = round(dayTempN, 2);
            allData[i] = String.valueOf(dayTemp);
        }
        for (int i = 7; i < 14; i++) {
            JSONObject day = days.getJSONObject(i - 7);
            JSONArray weather = day.getJSONArray("weather");
            JSONObject weatherInfor = weather.getJSONObject(0);
            String imageID = weatherInfor.getString("id");
            allData[i] = imageID;
        }
        return allData;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}
