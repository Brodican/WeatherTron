package com.example.utkua.weathertron;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.utkua.weathertron.Data.CityModel;
import com.example.utkua.weathertron.Utilities.JsonUtilities;
import com.example.utkua.weathertron.Utilities.NetworkUtilities;
import com.example.utkua.weathertron.Utilities.SwipeDismissTouchListener;

import java.net.URL;
import java.util.LinkedList;
import java.util.Map;

public class ChooseCityActivity extends AppCompatActivity {

    private final String TAG = "ChooseCityActivity";

    private EditText mInputET;
    private ListView mCitiesLV;
    private LinkedList<String> cities = new LinkedList();
    private LinkedList<CityModel> cityModels = new LinkedList();
    private ProgressBar mCitiesProgress;

    private CityModel cityModel;
    private String lowerCaseCityName;

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

        // Code for swipe removal
        ListView listView = mCitiesLV;
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    String city = cityModelListAdapter.getItem(position).getName();
                                    lowerCaseCityName = Character.toLowerCase(city.charAt(0)) + city.substring(1);
                                    cityModelListAdapter.remove(cityModelListAdapter.getItem(position));
                                    SharedPreferences preferences = ChooseCityActivity.this.getPreferences(Context.MODE_PRIVATE);
                                    preferences.edit().remove(lowerCaseCityName).commit();
                                }
                                cityModelListAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

        // Set up normal ViewGroup example
        final ViewGroup dismissableContainer = (ViewGroup) findViewById(R.id.city_choose_model_LL);
        for (int i = 0; i < cityModels.size(); i++) {
            final Button dismissableButton = new Button(this);
            dismissableButton.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dismissableButton.setText("Button " + (i + 1));
            dismissableButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ChooseCityActivity.this,
                            "Clicked " + ((Button) view).getText(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            // Create a generic swipe-to-dismiss touch listener.
            dismissableButton.setOnTouchListener(new SwipeDismissTouchListener(
                    dismissableButton,
                    null,
                    new SwipeDismissTouchListener.DismissCallbacks() {
                        @Override
                        public boolean canDismiss(Object token) {
                            return true;
                        }

                        @Override
                        public void onDismiss(View view, Object token) {
                            dismissableContainer.removeView(dismissableButton);
                        }
                    }));
            dismissableContainer.addView(dismissableButton);
        }
    }

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
