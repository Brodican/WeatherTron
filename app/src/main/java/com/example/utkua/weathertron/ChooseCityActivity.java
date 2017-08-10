package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
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
    private ProgressBar mCitiesProgress;

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
                addCities();
            }
        });
        addCities();
        mCitiesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = mCitiesLV.getItemAtPosition(position);
                String city = o.toString();
                Toast.makeText(ChooseCityActivity.this, city, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChooseCityActivity.this, ChosenCityActivity.class);
                intent.putExtra("location", city);
                startActivity(intent);
            }
        });
    }

    private void addCities () {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            if (cities.contains(entry.getValue().toString())) {

            }
            else {
                String cityCapitalised = entry.getValue().toString().substring(0, 1).toUpperCase() + entry.getValue().toString().substring(1);
                cities.add(cityCapitalised);
            }
        }
        CityListAdapter adapter = new CityListAdapter(this, R.layout.mycitylist, cities); // ChosenCityActivity set
        mCitiesLV.setAdapter(adapter);
    }
}
