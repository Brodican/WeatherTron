package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utkua.weathertron.Data.CityModel;
import com.example.utkua.weathertron.Data.WeatherModel;
import com.example.utkua.weathertron.Utilities.WeatherUtilities;

import java.util.List;

/**
 * Created by utkua on 8/9/2017.
 */

public class CityListAdapter extends ArrayAdapter{

    private final Activity context;

    public CityListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List cities) {
        super(context, resource, cities);
        this.context = context;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mycitylist, null,true);

        TextView cityChoose = rowView.findViewById(R.id.city_choose_TV);

        String cityName = (String) getItem(position);
        cityChoose.setText(cityName);

        return rowView;
    }
}
