package com.example.utkua.weathertron.Utilities;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by utkua on 8/7/2017.
 */

public class NetworkUtilities {

    public static URL buildUrl(Double lat, Double lon) {
        String testUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" +lat+ "&lon=" +lon+ "&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in buildURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildUrl(String location) {
        String testUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in buildURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL build7DayUrl(Double lat, Double lon) {
        String testUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" +lat+ "&lon=" +lon+ "&cnt=9&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in build7DayURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL build7DayUrl(String location) {
        String testUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + location + "&cnt=9&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in build7DayURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildAllDayUrl(Double lat, Double lon) {
        String testUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&cnt=16&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in buildAllDayURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL buildAllDayUrl(String location) {
        String testUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + location + "&cnt=16&appid=983bd13cdd1059ecc26b300b2a062f42";

        URL url = null;

        try {
            url = new URL(testUrl);
            Log.d(TAG, "Url in buildAllDayURL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            // InputStream for errors
            InputStream errorStream = urlConnection.getErrorStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            int data = inputStreamReader.read();
            while (data != -1) {
                char current = (char) data;
                result += current;
                data = inputStreamReader.read();
            }
        } catch (Exception e) {
            Log.d(TAG, "Error in NetworkUtilities.getResponseFromHttpUrl");
        }

        return result;
    }
}
