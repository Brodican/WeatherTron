package com.example.utkua.weathertron;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utkua.weathertron.Data.WeatherModel;
import com.example.utkua.weathertron.Utilities.JsonUtilities;
import com.example.utkua.weathertron.Utilities.NetworkUtilities;
import com.example.utkua.weathertron.Utilities.WeatherUtilities;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.R.attr.visibility;

public class ChosenCityActivity extends AppCompatActivity {

    private TextView mWeatherTV;
    private TextView mCurrentTV;
    private EditText mInputET;
    private ProgressBar mWeatherPB;
    public ListView list;

    private List<WeatherModel> weatherModels = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationClient;

    private String inputLocation = "";
    private String currentDescription = "";
    private String currentTempMax;
    private String currentTempMin;
    private String mCurrentTemp;
    private String mVisibility;
    private String mWindSpeed;
    private String mWindAngle;
    private String mSunrise;
    private String mSunset;
    private String mPressure;
    private String mHumidity;
    private String mRain;
    private String mSnow;

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

        final Intent intent = new Intent();

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
                            if (intent.getExtras() == null) {
                                loadWeatherDataCoordinatesCurrent(lat, lon);
                                loadWeatherDataCoordinates7Day(lat, lon);
                                loadWeatherDataCoordinatesAllDay(lat, lon);
                            }
                            else {
                                Bundle locationB = intent.getExtras();
                                String locationS = locationB.getString("location");
                                loadWeatherDataStringCurrent(locationS);
                                loadWeatherDataString7Day(locationS);
                                loadWeatherDataStringAllDay(locationS);
                            }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.choose_city:
                //your code
                // EX : call intent if you want to swich to other activity
                return true;
        }
        return false;
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

    private void loadWeatherDataStringCurrent(String location) {
        Log.d(TAG, "loadWeatherData called");
        // AsyncTask which loads weather data in background, using location param
        new FetchWeatherTaskStringCurrent().execute(location);
    }

    private void loadWeatherDataString7Day(String location) {
        new FetchWeatherTaskString7Day().execute(location);
    }

    private void loadWeatherDataStringAllDay(String location) {
        new FetchWeatherTaskStringAllDay().execute(location);
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
                TextView curDay = (TextView) findViewById(R.id.current_day);
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK);
                String dateStr = "null";
                WeatherModel dayInfo = new WeatherModel();
                switch (day) {
                    case Calendar.SUNDAY:
                        dateStr = "Sunday"; break;
                    case Calendar.MONDAY:
                        dateStr = "Monday"; break;
                    case Calendar.TUESDAY:
                        dateStr = "Tuesday"; break;
                    case Calendar.WEDNESDAY:
                        dateStr = "Wednesday"; break;
                    case Calendar.THURSDAY:
                        dateStr = "Thursday"; break;
                    case Calendar.FRIDAY:
                        dateStr = "Friday"; break;
                    case Calendar.SATURDAY:
                        dateStr = "Saturday"; break;
                }
                curDay.setText(dateStr);
                currentDescription = weatherData[4];
                currentTempMin = weatherData[1];
                currentTempMax = weatherData[2];
                curMinMax.setText(Html.fromHtml("<font color=#cc0029>" + currentTempMax + "</font> <font color=#0516DE>" + currentTempMin + "</font>"));
                TextView curTemp = (TextView) findViewById(R.id.current_loc_temp);
                curTemp.setText(weatherData[0]);
                mCurrentTemp = weatherData[0];

                mVisibility = weatherData[5];
                mWindSpeed = weatherData[6];
                mWindAngle = weatherData[7];
                mSunrise = weatherData[8];
                mSunset = weatherData[9];
                mPressure = weatherData[10];
                mHumidity = weatherData[11];
                if (weatherData[12] != null)
                    mRain = weatherData[12];

                if (weatherData[13] != null)
                    mSnow = weatherData[13];

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class FetchWeatherTaskStringCurrent extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {

            Log.d(TAG, "doInBackground called");

            /* If there's no zip code, there's nothing to look up. */
            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];

            URL weatherRequestUrlCurrent = NetworkUtilities.buildUrl(location);

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
                TextView curDay = (TextView) findViewById(R.id.current_day);
                Calendar c = Calendar.getInstance();
                int day = c.get(Calendar.DAY_OF_WEEK);
                String dateStr = "null";
                WeatherModel dayInfo = new WeatherModel();
                switch (day) {
                    case Calendar.SUNDAY:
                        dateStr = "Sunday"; break;
                    case Calendar.MONDAY:
                        dateStr = "Monday"; break;
                    case Calendar.TUESDAY:
                        dateStr = "Tuesday"; break;
                    case Calendar.WEDNESDAY:
                        dateStr = "Wednesday"; break;
                    case Calendar.THURSDAY:
                        dateStr = "Thursday"; break;
                    case Calendar.FRIDAY:
                        dateStr = "Friday"; break;
                    case Calendar.SATURDAY:
                        dateStr = "Saturday"; break;
                }
                curDay.setText(dateStr);
                currentDescription = weatherData[4];
                currentTempMin = weatherData[1];
                currentTempMax = weatherData[2];
                curMinMax.setText(Html.fromHtml("<font color=#cc0029>" + currentTempMax + "</font> <font color=#0516DE>" + currentTempMin + "</font>"));
                TextView curTemp = (TextView) findViewById(R.id.current_loc_temp);
                curTemp.setText(weatherData[0]);
                mCurrentTemp = weatherData[0];

                mVisibility = weatherData[5];
                mWindSpeed = weatherData[6];
                mWindAngle = weatherData[7];
                mSunrise = weatherData[8];
                mSunset = weatherData[9];
                mPressure = weatherData[10];
                mHumidity = weatherData[11];
                if (weatherData[12] != null)
                    mRain = weatherData[12];

                if (weatherData[13] != null)
                    mSnow = weatherData[13];

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

//                allData[6] = windSpeed;
//                allData[7] = windAngle;
//                allData[8] = sunrise;
//                allData[9] = sunset;
//                allData[10] = pressure;
//                allData[11] = humidity;

                Float windSpd = Float.valueOf(mWindSpeed);
                Float windAng = Float.valueOf(mWindAngle);
                String humidity = mHumidity + "%";
                String precipitation = "0cm";
                String pressure = mPressure + "hPa";

                if (mRain != null) {
                    precipitation = mRain + "%";
                }

                if (mSnow != null) {
                    precipitation = mSnow + "%";
                }

                Integer vis = Integer.parseInt(mVisibility)/1000;
                String visS = vis + "km";
                String formattedWind = WeatherUtilities.getFormattedWind(ChosenCityActivity.this, windSpd, windAng);

                long sunriseL = Long.parseLong(mSunrise);
                Date sunrise = new Date(sunriseL * 1000);

                long sunsetL = Long.parseLong(mSunset);
                Date sunset = new Date(sunsetL * 1000);

                Calendar cal = Calendar.getInstance();
                cal.setTime(sunrise);
                Integer sunriseHI = cal.get(Calendar.HOUR_OF_DAY);
                String formattedSunriseH = String.format("%02d", sunriseHI);
                Integer sunriseMI = cal.get(Calendar.MINUTE);
                String formattedSunriseM = String.format("%02d", sunriseMI);

                String formattedSunrise = formattedSunriseH + ":" + formattedSunriseM;

                cal.setTime(sunset);
                Integer sunsetHI = cal.get(Calendar.HOUR_OF_DAY);
                String formattedSunsetH = String.format("%02d", sunsetHI);
                Integer sunsetMI = cal.get(Calendar.MINUTE);
                String formattedSunsetM = String.format("%02d", sunsetMI);

                String formattedSunset = formattedSunsetH + ":" + formattedSunsetM;

                DaysListAdapter adapter = new DaysListAdapter(ChosenCityActivity.this, weatherModels); // ChosenCityActivity set
                list = (ListView)findViewById(R.id.days_LV);
                list.setAdapter(adapter);
                WeatherModel  model = new WeatherModel();
                model.setDayInfo("Today: Current weather state: " + currentDescription + ". The maximum temperature today is: "
                        + currentTempMax + ". The minimum temperature tonight is: " + currentTempMin + "." +
                        "\n\n    Sunrise: " + formattedSunrise + "\n    Sunset: " + formattedSunset  +
                        "\n\n    Wind speed and direction: " + formattedWind +
                        "\n\n    Precipitation: " + precipitation + "\n    Humidity: " + humidity +
                        "\n\n    Pressure: " + pressure + "\n    Visibility: " + visS);
                WeatherModel separatorModel = new WeatherModel();
                // Organise separators
                weatherModels.add(separatorModel);
                weatherModels.add(model);
                weatherModels.add(separatorModel);

                mWeatherPB.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class FetchWeatherTaskString7Day extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {

            Log.d(TAG, "doInBackground called - Coordinates7Day");

            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];

            URL weatherRequestUrl7Day = NetworkUtilities.build7DayUrl(location);

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

//                allData[6] = windSpeed;
//                allData[7] = windAngle;
//                allData[8] = sunrise;
//                allData[9] = sunset;
//                allData[10] = pressure;
//                allData[11] = humidity;

                Float windSpd = Float.valueOf(mWindSpeed);
                Float windAng = Float.valueOf(mWindAngle);
                String humidity = mHumidity + "%";
                String precipitation = "0cm";
                String pressure = mPressure + "hPa";

                if (mRain != null) {
                    precipitation = mRain + "%";
                }

                if (mSnow != null) {
                    precipitation = mSnow + "%";
                }

                Integer vis = Integer.parseInt(mVisibility)/1000;
                String visS = vis + "km";
                String formattedWind = WeatherUtilities.getFormattedWind(ChosenCityActivity.this, windSpd, windAng);

                long sunriseL = Long.parseLong(mSunrise);
                Date sunrise = new Date(sunriseL * 1000);

                long sunsetL = Long.parseLong(mSunset);
                Date sunset = new Date(sunsetL * 1000);

                Calendar cal = Calendar.getInstance();
                cal.setTime(sunrise);
                Integer sunriseHI = cal.get(Calendar.HOUR_OF_DAY);
                String formattedSunriseH = String.format("%02d", sunriseHI);
                Integer sunriseMI = cal.get(Calendar.MINUTE);
                String formattedSunriseM = String.format("%02d", sunriseMI);

                String formattedSunrise = formattedSunriseH + ":" + formattedSunriseM;

                cal.setTime(sunset);
                Integer sunsetHI = cal.get(Calendar.HOUR_OF_DAY);
                String formattedSunsetH = String.format("%02d", sunsetHI);
                Integer sunsetMI = cal.get(Calendar.MINUTE);
                String formattedSunsetM = String.format("%02d", sunsetMI);

                String formattedSunset = formattedSunsetH + ":" + formattedSunsetM;

                DaysListAdapter adapter = new DaysListAdapter(ChosenCityActivity.this, weatherModels); // ChosenCityActivity set
                list = (ListView)findViewById(R.id.days_LV);
                list.setAdapter(adapter);
                WeatherModel  model = new WeatherModel();
                model.setDayInfo("Today: Current weather state: " + currentDescription + ". The maximum temperature today is: "
                        + currentTempMax + ". The minimum temperature tonight is: " + currentTempMin + "." +
                        "\n\n    Sunrise: " + formattedSunrise + "\n    Sunset: " + formattedSunset  +
                        "\n\n    Wind speed and direction: " + formattedWind +
                        "\n\n    Precipitation: " + precipitation + "\n    Humidity: " + humidity +
                        "\n\n    Pressure: " + pressure + "\n    Visibility: " + visS);
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

            URL weatherRequestUrlAllDay = NetworkUtilities.buildAllDayUrl(lat, lon);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrlAllDay);

                String[] simpleJsonWeatherDataCurrent = JsonUtilities
                        .getAllDayFromJson(jsonWeatherResponse);

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
                Log.d(TAG, "weatherData is not null allDays");

                Calendar c = Calendar.getInstance();
                int currentHour = c.get(Calendar.HOUR_OF_DAY) + 3;

                String space1 = "\u200A";
                String space2 = "\u2002";

                ImageView allDaysIV = (ImageView) findViewById(R.id.allDays_IV1);

                TextView allDaysTV = (TextView) findViewById(R.id.allDays_TV);
                allDaysTV.append("   ");
                allDaysTV.append("Now");
                allDaysTV.append("              ");
                TextView allDaysBottomTV = (TextView) findViewById(R.id.allDays_bottom_TV);
                allDaysBottomTV.append("    ");
                allDaysBottomTV.append(mCurrentTemp);
                allDaysBottomTV.append("                " + space1);

                int[] ids = {R.id.allDays_IV1, R.id.allDays_IV2, R.id.allDays_IV3, R.id.allDays_IV4, R.id.allDays_IV5, R.id.allDays_IV6, R.id.allDays_IV7, R.id.allDays_IV8, R.id.allDays_IV9,
                        R.id.allDays_IV10, R.id.allDays_IV11, R.id.allDays_IV12, R.id.allDays_IV13, R.id.allDays_IV14, R.id.allDays_IV15, R.id.allDays_IV16, R.id.allDays_IV17};

                allDaysIV.setImageResource(WeatherUtilities.getIconResourceForWeatherCondition(Integer.parseInt(weatherData[16])));

                for (int i = 0; i < 16; i++) {
                    if (currentHour > 24) {
                        currentHour-=24;
                    }
                    allDaysIV = (ImageView) findViewById(ids[i+1]);
                    String formattedHour = String.format("%02d", currentHour);
                    String formattedTemp = weatherData[i].concat("°");
                    allDaysIV.setImageResource(WeatherUtilities.getIconResourceForWeatherCondition(Integer.parseInt(weatherData[i+16])));
                    allDaysBottomTV.append(formattedTemp + "                " + space1);
                    allDaysTV.append(formattedHour + "                " + space2);
                    currentHour+=3;
                }
            }
        }
    }

    public class FetchWeatherTaskStringAllDay extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWeatherPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String ... strings) {

            Log.d(TAG, "doInBackground called - CoordinatesAllDay");

            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];

            URL weatherRequestUrlAllDay = NetworkUtilities.buildAllDayUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrlAllDay);

                String[] simpleJsonWeatherDataCurrent = JsonUtilities
                        .getAllDayFromJson(jsonWeatherResponse);

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
                Log.d(TAG, "weatherData is not null allDays");

                Calendar c = Calendar.getInstance();
                int currentHour = c.get(Calendar.HOUR_OF_DAY) + 3;

                String space1 = "\u200A";
                String space2 = "\u2002";

                ImageView allDaysIV = (ImageView) findViewById(R.id.allDays_IV1);

                TextView allDaysTV = (TextView) findViewById(R.id.allDays_TV);
                allDaysTV.append("   ");
                allDaysTV.append("Now");
                allDaysTV.append("              ");
                TextView allDaysBottomTV = (TextView) findViewById(R.id.allDays_bottom_TV);
                allDaysBottomTV.append("    ");
                allDaysBottomTV.append(mCurrentTemp);
                allDaysBottomTV.append("                " + space1);

                int[] ids = {R.id.allDays_IV1, R.id.allDays_IV2, R.id.allDays_IV3, R.id.allDays_IV4, R.id.allDays_IV5, R.id.allDays_IV6, R.id.allDays_IV7, R.id.allDays_IV8, R.id.allDays_IV9,
                        R.id.allDays_IV10, R.id.allDays_IV11, R.id.allDays_IV12, R.id.allDays_IV13, R.id.allDays_IV14, R.id.allDays_IV15, R.id.allDays_IV16, R.id.allDays_IV17};

                allDaysIV.setImageResource(WeatherUtilities.getIconResourceForWeatherCondition(Integer.parseInt(weatherData[16])));

                for (int i = 0; i < 16; i++) {
                    if (currentHour > 24) {
                        currentHour-=24;
                    }
                    allDaysIV = (ImageView) findViewById(ids[i+1]);
                    String formattedHour = String.format("%02d", currentHour);
                    String formattedTemp = weatherData[i].concat("°");
                    allDaysIV.setImageResource(WeatherUtilities.getIconResourceForWeatherCondition(Integer.parseInt(weatherData[i+16])));
                    allDaysBottomTV.append(formattedTemp + "                " + space1);
                    allDaysTV.append(formattedHour + "                " + space2);
                    currentHour+=3;
                }
            }
        }
    }
}
