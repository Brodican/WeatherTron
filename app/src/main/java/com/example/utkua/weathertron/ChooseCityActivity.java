package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utkua.weathertron.Data.CityModel;
import com.example.utkua.weathertron.R;
import com.example.utkua.weathertron.Utilities.JsonUtilities;
import com.example.utkua.weathertron.Utilities.NetworkUtilities;
import com.example.utkua.weathertron.Utilities.WeatherUtilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChooseCityActivity extends AppCompatActivity {

    private final String TAG = "ChooseCityActivity";

    private EditText mInputET;
    private ListView mCitiesLV;
    private LinkedList<String> cities = new LinkedList();
    private LinkedList<CityModel> cityModels = new LinkedList();
    private ProgressBar mCitiesProgress;

    private CityModel cityModel;
    private String cityNameforDialog;

    CityModelListAdapter cityModelListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        FloatingActionButton floaterButt = (FloatingActionButton) findViewById(R.id.city_choose_FAB);
        mInputET = (EditText) findViewById(R.id.choose_city_ET);
        mCitiesLV = (ListView) findViewById(R.id.saved_cities_LV);

        floaterButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationString = mInputET.getText().toString();
                SharedPreferences sharedPref = ChooseCityActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(locationString, locationString);
                editor.commit();
                ((EditText) findViewById(R.id.choose_city_ET)).getText().clear();
                addCities();
            }
        });
        addCities();
        mCitiesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityModel = (CityModel) parent.getAdapter().getItem(position);

                TextView tv = view.findViewById(R.id.city_choose_TV);
                String city = tv.getText().toString();
                cityNameforDialog = Character.toLowerCase(city.charAt(0)) + city.substring(1);
                Toast.makeText(ChooseCityActivity.this, city, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ChooseCityActivity.this);
                builder.setMessage("U sure fam?!").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        mCitiesLV.setOnTouchListener(new OnSwipeTouchListener(ChooseCityActivity.this) {
            public void onSwipeTop() {
                Toast.makeText(ChooseCityActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(ChooseCityActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(ChooseCityActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(ChooseCityActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    SharedPreferences preferences = ChooseCityActivity.this.getPreferences(Context.MODE_PRIVATE);
                    cityModelListAdapter.remove(cityModel);
                    preferences.edit().remove(cityNameforDialog).commit();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void addCities () {
        cityModelListAdapter = new CityModelListAdapter(this, R.layout.mycitylist, cityModels);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            boolean flag = false;
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            for (int i = 0; i < cityModels.size(); i++) {
                Log.d(TAG, "Size: " + cityModels.size());
                if (cityModels.get(i).getName().equals(entry.getValue().toString())) {
                    Log.d(TAG, "Equal city found");
                    flag = true;
                }
            }
            if (!flag){
                new FetchWeatherTaskTempOnly().execute(entry.getValue().toString());
            }
        }
//        CityListAdapter adapter = new CityListAdapter(this, R.layout.mycitylist, cities);
        mCitiesLV.setAdapter(cityModelListAdapter);
    }

    public class FetchWeatherTaskTempOnly extends AsyncTask<String, Void, String[]> {

        String cityCapitalised;
        String cityNCapitalised;

        @Override
        protected String[] doInBackground(String... strings) {

            Log.d(TAG, "doInBackground called");
            cityCapitalised = strings[0].substring(0, 1).toUpperCase() + strings[0].substring(1);
            cityNCapitalised = strings[0];

            /* If there's no zip code, there's nothing to look up. */
            if (strings.length == 0) {
                return null;
            }

            URL weatherRequestUrlCurrent = NetworkUtilities.buildUrl(strings[0]);

            try {
                String jsonWeatherResponse = NetworkUtilities
                        .getResponseFromHttpUrl(weatherRequestUrlCurrent);

                String[] simpleJsonWeatherDataTemp = JsonUtilities
                        .getTempOnlyFromJson(jsonWeatherResponse);

                return simpleJsonWeatherDataTemp;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] allData) {

            super.onPostExecute(allData);

            String time = allData[1];
            String temp = allData[0];

            Log.d(TAG, "Time in choose: " + time);

            CityModel city = new CityModel(cityNCapitalised, time, temp);
            cityModels.add(city);
            cityModelListAdapter.notifyDataSetChanged();
        }

    }
}
