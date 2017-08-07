package com.example.utkua.weathertron;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utkua.weathertron.Utilities.JsonUtilities;
import com.example.utkua.weathertron.Utilities.NetworkUtilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChosenCityActivity extends AppCompatActivity {

    private TextView mWeatherTV;
    private TextView mCurrentTV;
    private EditText mInputET;
    private ProgressBar mWeatherPB;
    public ListView list;

    private FusedLocationProviderClient mFusedLocationClient;

    private String inputLocation = "";

    public static final String TAG = "ChosenCityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_city);

        // Find weather_tv TextView and store it in mWeatherTV member variable
//        mWeatherTV = (TextView) findViewById(R.id.days_textview);
        mCurrentTV = (TextView) findViewById(R.id.current_location);
//        mInputET = (EditText) findViewById(R.id.input_get_location);
        mWeatherPB = (ProgressBar) findViewById(R.id.loading_weather);
//        FloatingActionButton floaterButt = (FloatingActionButton) findViewById(R.id.button_send_input_location);
//
//        floaterButt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Set inputLocation to the location which was input by the user
//                String locationString = mInputET.getText().toString();
//                // Loads weather data
//                loadWeatherDataString(locationString);
//            }
//        });

        // Assign value to mFusedLocationClient with LocationServices
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChosenCityActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Double lat = location.getLatitude();
                            Double lon = location.getLongitude();
                            Log.d(TAG, "Lat: " + lat + " Lon: " + lon);
                            loadWeatherDataCoordinatesCurrent(lat, lon);
                            loadWeatherDataCoordinates7Day(lat, lon);
                        }
                    }
                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(ChosenCityActivity.this, "Permission denied to get your location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void loadWeatherDataString(String locationString) {
        Log.d(TAG, "loadWeatherData called");
        // AsyncTask which loads weather data in background, using location param
        new FetchWeatherTaskString().execute(locationString);
    }

    private void loadWeatherDataCoordinatesCurrent(Double lat, Double lon) {
        Log.d(TAG, "loadWeatherData called");
        // AsyncTask which loads weather data in background, using location param
        new FetchWeatherTaskCoordinatesCurrent().execute(lat, lon);
    }

    private void loadWeatherDataCoordinates7Day(Double lat, Double lon) {
        new FetchWeatherTaskCoordinates7Day().execute(lat, lon);
    }

    public class FetchWeatherTaskString extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            Log.d(TAG, "doInBackground called");

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }
            String loc = params[0];
            URL weatherRequestUrl = NetworkUtilities.buildUrl(loc);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrl);

                String[] simpleJsonWeatherData = JsonUtilities
                        .getCurrentFromJson(jsonWeatherResponse);

                return simpleJsonWeatherData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            Log.d(TAG, "onPostExecute called");
            if (weatherData != null) {
                Log.d(TAG, "weatherData is not null");
                /*
                 * Iterate through the array and append the Strings to the TextView. The reason why we add
                 * the "\n\n\n" after the String is to give visual paration between each String in the
                 * TextView. Later, we'll learn about a better way to display lists of data.
                 */
                for (String weatherString : weatherData) {
                    Log.d(TAG, "weatherString is: " + weatherString);
                    mWeatherTV.append((weatherString) + "\n\n\n");
                }
                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class FetchWeatherTaskCoordinatesCurrent extends AsyncTask<Double, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Double... doubles) {

            Log.d(TAG, "doInBackground called");

            /* If there's no zip code, there's nothing to look up. */
            if (doubles.length == 0) {
                return null;
            }

            Double lat = doubles[0];
            Double lon = doubles[1];

            URL weatherRequestUrlCurrent = NetworkUtilities.buildUrl(lat, lon);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrlCurrent);

                String[] simpleJsonWeatherDataCurrent = JsonUtilities
                        .getCurrentFromJson(jsonWeatherResponse);

                return simpleJsonWeatherDataCurrent;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            Log.d(TAG, "onPostExecute called");
            if (weatherData != null) {
                Log.d(TAG, "weatherData is not null");

                TextView curLoc = (TextView) findViewById(R.id.current_location);
                curLoc.setText(weatherData[1]);
                TextView curDesc = (TextView) findViewById(R.id.current_loc_description);
                curDesc.setText(weatherData[2]);
                TextView curTemp = (TextView) findViewById(R.id.current_loc_temp);
                curTemp.setText(weatherData[0]);

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class FetchWeatherTaskCoordinates7Day extends AsyncTask<Double, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Double... doubles) {

            Log.d(TAG, "doInBackground called - Coordinates7Day");

            /* If there's no zip code, there's nothing to look up. */
            if (doubles.length == 0) {
                return null;
            }

            Double lat = doubles[0];
            Double lon = doubles[1];

            URL weatherRequestUrl7Day = NetworkUtilities.build7DayUrl(lat, lon);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrl7Day);

                String[] simpleJsonWeatherDataCurrent = JsonUtilities
                        .get7DaysFromJson(jsonWeatherResponse);

                return simpleJsonWeatherDataCurrent;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            Log.d(TAG, "onPostExecute called");
            if (weatherData != null) {
                Log.d(TAG, "weatherData is not null");

//                TextView daysTV = (TextView) findViewById(R.id.days_textview);
                String[] dayInfo = new String[7];
                Integer[] iconIds = new Integer[7];

                DateFormat dateFormat = new SimpleDateFormat("MM/dd");
                Date date = new Date();
                String dateStr;
                Calendar c = Calendar.getInstance();
                c.setTime(date);

                for (int i = 0; i < 7; i++) {
                    String temp = weatherData[i];
                    dateStr = dateFormat.format(date);
                    dayInfo[i] = dateStr +": "+ temp + "\n\n\n";
                    c.add(Calendar.DATE, 1);
                    date = c.getTime();
                }

                for (int i = 0; i < 7; i++) {
                    int id = Integer.parseInt(weatherData[i+7]);
                    iconIds[i] = id;
                }

                DaysListAdapter adapter = new DaysListAdapter(ChosenCityActivity.this, dayInfo, iconIds); // Mainactivity set
                list = (ListView)findViewById(R.id.days_LV);
                list.setAdapter(adapter);

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }
}
