package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utkua.weathertron.Data.CityModel;
import com.google.android.gms.vision.text.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

/**
 * Created by utkua on 8/10/2017.
 */

public class CityModelListAdapter extends ArrayAdapter<CityModel> {

    private static final String TAG = "CityModelListAdapter";

    private Activity context;

    public CityModelListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<CityModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mycitylist, null, true);

        TextView cityChoose = rowView.findViewById(R.id.city_choose_TV);
        TextView cityTemp = rowView.findViewById(R.id.city_choose_temp_TV);
        TextView cityTimeTV = rowView.findViewById(R.id.city_choose_time_TV);

        String formattedCityTime = null;
        Log.d(TAG, "Time in choose adapter: " + getItem(position).getTime());
        if (getItem(position).getTime() != null) {

            long cityTimeL = Long.parseLong(getItem(position).getTime());
            Date cityTime = new Date(cityTimeL * 1000);

            Calendar cal = Calendar.getInstance();
            cal.setTime(cityTime);
            Integer cityTimeHI = cal.get(Calendar.HOUR_OF_DAY);
            String formattedCityTimeH = String.format("%02d", cityTimeHI);
            Integer cityTimeMI = cal.get(Calendar.MINUTE);
            String formattedCityTimeM = String.format("%02d", cityTimeMI);
            formattedCityTime = formattedCityTimeH + ":" + formattedCityTimeM;
        }


        String cityName = getItem(position).getName().substring(0, 1).toUpperCase() + getItem(position).getName().substring(1);
        String temp = getItem(position).getTemp();
        cityChoose.setText(cityName);
        cityTemp.setText(temp);
        cityTimeTV.setText(formattedCityTime);

        cityChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChosenCityActivity.class);
                intent.putExtra("location", getItem(position).getName());
                Toast.makeText(context, getItem(position).getName(), Toast.LENGTH_SHORT).show();
                context.startActivity(intent);
                context.finish();
            }
        });

        return rowView;
    }
}
