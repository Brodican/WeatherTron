package com.example.utkua.weathertron;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utkua.weathertron.Data.AllDayModel;
import com.example.utkua.weathertron.Data.WeatherModel;
import com.example.utkua.weathertron.Utilities.JsonUtilities;
import com.example.utkua.weathertron.Utilities.NetworkUtilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChosenCityActivity extends AppCompatActivity {

    private TextView mWeatherTV;
    private TextView mCurrentTV;
    private EditText mInputET;
    private ProgressBar mWeatherPB;
    public ListView list;

    private List<WeatherModel> weatherModels = new ArrayList<>();
    private List<AllDayModel> allDayModels = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;

    LinearLayoutManager mLayoutManager;
    RecyclerView mAllDaysList;
    AllDayAdapter mDayAdapter;

    private String inputLocation = "";
    private String currentDescription = "";
    private String currentTempMax;
    private String currentTempMin;

    public static final String TAG = "ChosenCityActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_city);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mAllDaysList = (RecyclerView) findViewById(R.id.allDays_RV_hor);
        mAllDaysList.setLayoutManager(mLayoutManager);

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

        mDayAdapter = new AllDayAdapter(allDayModels, R.layout.my_horizontal_list);
        mAllDaysList.setAdapter(mDayAdapter);
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

    private void loadWeatherDataCoordinatesAllDay(Double lat, Double lon) {
        new FetchWeatherTaskCoordinatesAllDay().execute(lat, lon);
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
                curLoc.setText(weatherData[3]);
                TextView curDesc = (TextView) findViewById(R.id.current_loc_description);
                curDesc.setText(weatherData[4]);
                TextView curMinMax = (TextView) findViewById(R.id.current_temp_max_min);
                currentDescription = weatherData[4];
                currentTempMin = weatherData[1];
                currentTempMax = weatherData[2];
                curMinMax.setText(Html.fromHtml("<font color=#cc0029>" + currentTempMax + "</font> <font color=#0516DE>" + currentTempMin + "</font>"));
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

                String dateStr = "";
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK) + 1;

                for (int i = 0; i < 8; i++) {
                    WeatherModel dayInfo = new WeatherModel();
                    switch (day) {
                        case Calendar.SUNDAY:
                            dateStr = "Sunday"; day++; break;
                        case Calendar.MONDAY:
                            dateStr = "Monday"; day++; break;
                        case Calendar.TUESDAY:
                            dateStr = "Tuesday"; day++; break;
                        case Calendar.WEDNESDAY:
                            dateStr = "Wednesday"; day++; break;
                        case Calendar.THURSDAY:
                            dateStr = "Thursday"; day++; break;
                        case Calendar.FRIDAY:
                            dateStr = "Friday"; day++; break;
                        case Calendar.SATURDAY: {
                            dateStr = "Saturday";
                            day = 1;
                            break;
                        }
                    }
                    dayInfo.setMinTempInfo(Integer.valueOf(weatherData[i]));
                    dayInfo.setMaxTempInfo(Integer.valueOf(weatherData[i+8]));
                    dayInfo.setDayInfo(dateStr);
                    dayInfo.setImgId(Integer.parseInt(weatherData[i+16]));
                    weatherModels.add(dayInfo);

                }


                DaysListAdapter adapter = new DaysListAdapter(ChosenCityActivity.this, weatherModels); // ChosenCityActivity set
                list = (ListView)findViewById(R.id.days_LV);
                list.setAdapter(adapter);
                WeatherModel  model = new WeatherModel();
                model.setDayInfo("Today: Current weather state: " + currentDescription + ". The maximum temperature today is: "
                        + currentTempMax + ". The minimum temperature tonight is: " + currentTempMin + ".");
                WeatherModel separatorModel = new WeatherModel();
                // Organise separators
                weatherModels.add(separatorModel);
                weatherModels.add(model);
                weatherModels.add(separatorModel);

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class FetchWeatherTaskCoordinatesAllDay extends AsyncTask<Double, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Double... doubles) {

            Log.d(TAG, "doInBackground called - CoordinatesAllDay");

            if (doubles.length == 0) {
                return null;
            }

            Double lat = doubles[0];
            Double lon = doubles[1];

            URL weatherRequestUrl7Day = NetworkUtilities.buildAllDayUrl(lat, lon);

            try {
//                String jsonWeatherResponse = NetworkUtilities
//                        .getResponseFromHttpUrl(weatherRequestUrl7Day);
//
//                String[] simpleJsonWeatherDataCurrent = JsonUtilities
//                        .getAllDayFromJson(jsonWeatherResponse);

                return null;

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

                String dateStr = "";
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK) + 1;

                for (int i = 0; i < 8; i++) {
                    WeatherModel dayInfo = new WeatherModel();
                    switch (day) {
                        case Calendar.SUNDAY:
                            dateStr = "Sunday"; day++; break;
                        case Calendar.MONDAY:
                            dateStr = "Monday"; day++; break;
                        case Calendar.TUESDAY:
                            dateStr = "Tuesday"; day++; break;
                        case Calendar.WEDNESDAY:
                            dateStr = "Wednesday"; day++; break;
                        case Calendar.THURSDAY:
                            dateStr = "Thursday"; day++; break;
                        case Calendar.FRIDAY:
                            dateStr = "Friday"; day++; break;
                        case Calendar.SATURDAY: {
                            dateStr = "Saturday";
                            day = 1;
                            break;
                        }
                    }
                    dayInfo.setMinTempInfo(Integer.valueOf(weatherData[i]));
                    dayInfo.setMaxTempInfo(Integer.valueOf(weatherData[i+8]));
                    dayInfo.setDayInfo(dateStr);
                    dayInfo.setImgId(Integer.parseInt(weatherData[i+16]));
                    weatherModels.add(dayInfo);

                }


                DaysListAdapter adapter = new DaysListAdapter(ChosenCityActivity.this, weatherModels); // ChosenCityActivity set
                list = (ListView)findViewById(R.id.days_LV);
                list.setAdapter(adapter);
                WeatherModel model = new WeatherModel();
                model.setDayInfo("Today: Current weather state: " + currentDescription + ". The maximum temperature today is: "
                        + currentTempMax + ". The minimum temperature tonight is: " + currentTempMin + ".");
                WeatherModel separatorModel = new WeatherModel();
                // Organise separators
                weatherModels.add(separatorModel);
                weatherModels.add(model);
                weatherModels.add(separatorModel);

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
            TextView RV_TV = (TextView) findViewById(R.id.test_TV);
            AllDayModel dayModel = new AllDayModel(RV_TV);
            dayModel.setText("Testing");
            allDayModels.add(dayModel);

        }
    }
}
