package com.example.utkua.weathertron.Utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by utkua on 8/7/2017.
 */

public class JsonUtilities {

    private static String TAG = "JsonUtilities";

    public static String[] getCurrentFromJson(String forecastJsonStr)
            throws JSONException {
        // jsonObject containing all data, unformatted
        JSONObject jsonObject = new JSONObject(forecastJsonStr);
        String visibility = jsonObject.getString("visibility");
        // "main" gets main details from the sent JSONObject
        JSONObject weatherMain = new JSONObject(jsonObject.getString("main"));
        // JSONArray of weather
        JSONArray weatherWeath = jsonObject.getJSONArray("weather");
        JSONObject wind = jsonObject.getJSONObject("wind");
        String windSpeed = wind.getString("speed");
        String windAngle;
        if (wind.has("deg")) {
            windAngle = wind.getString("deg");
        }
        else {
            windAngle = "0";
        }
        JSONObject sys = jsonObject.getJSONObject("sys");
        String sunrise = sys.getString("sunrise");
        Log.d(TAG, "Sunrise: " + sunrise);
        String sunset = sys.getString("sunset");
        // Returns temperature in degrees Kelvin
        Double tempK = Double.parseDouble(weatherMain.getString("temp"));
        Double tempMaxK = Double.parseDouble(weatherMain.getString("temp_max"));
        Double tempMinK = Double.parseDouble(weatherMain.getString("temp_min"));
        String pressure = weatherMain.getString("pressure");
        String humidity = weatherMain.getString("humidity");
        // Cast to int to remove decimal
        int tempC = (int) (tempK - 273);
        int tempMaxC = (int) (tempMaxK - 273);
        int tempMinC = (int) (tempMinK - 273);
        // Gets name
        String locationName = jsonObject.getString("name");
        // Split JSONArray into sections
        JSONObject weatherObj = weatherWeath.getJSONObject(0);
        String weatherDescription = weatherObj.getString("description");
        String curId = weatherObj.getString("id");
        // Declare String array to hold data, place data in array
        String[] allData = new String[15];
        // Put temp, name, and weather description in array to be sent
        allData[0] = Integer.toString(tempC) + "°";
        allData[1] = Integer.toString(tempMinC);
        allData[2] = Integer.toString(tempMaxC);
        allData[3] = locationName;
        allData[4] = weatherDescription;
        allData[5] = visibility;
        allData[6] = windSpeed;
        allData[7] = windAngle;
        allData[8] = sunrise;
        allData[9] = sunset;
        allData[10] = pressure;
        allData[11] = humidity;
        allData[14] = curId;

        if (jsonObject.has("rain")) {
            JSONObject rainInfo = jsonObject.getJSONObject("rain");
            String rain = rainInfo.getString("3h");
            allData[12] = rain;
        }

        if (jsonObject.has("snow")) {
            JSONObject snowInfo = jsonObject.getJSONObject("rain");
            String snow = snowInfo.getString("3h");
            allData[13] = snow;
        }

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
        JSONArray hours = jsonObject.getJSONArray("list");
        String[] allData = new String[32];

        for (int i = 0; i < 16; i++) {
            JSONObject hour = hours.getJSONObject(i);
            JSONObject main = hour.getJSONObject("main");
            Double tempN = Double.parseDouble(main.getString("temp"));
            int temp = (int) (tempN - 273);
            allData[i] = String.valueOf(temp);
        }
        for (int i = 0; i < 16; i++) {
            JSONObject hour = hours.getJSONObject(i);
            JSONArray weatherInfo = hour.getJSONArray("weather");
            JSONObject imgIdObject = weatherInfo.getJSONObject(0);
            int imgId = Integer.valueOf(imgIdObject.getString("id"));
            allData[i+16] = String.valueOf(imgId);
        }
        return allData;
    }
}
