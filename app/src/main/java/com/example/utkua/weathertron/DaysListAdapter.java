package com.example.utkua.weathertron;

import android.app.Activity;
import android.graphics.Color;
import android.renderscript.Double2;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.utkua.weathertron.Data.WeatherModel;
import com.example.utkua.weathertron.Utilities.WeatherUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by utkua on 8/7/2017.
 */

public class DaysListAdapter extends ArrayAdapter<WeatherModel> {
    private final Activity context;
    private final String colourString;


    public static final String TAG = "DaysListActivity";

    public DaysListAdapter(Activity context, List<WeatherModel> datas, String mString) {
        super(context, R.layout.mylist, datas);
        this.colourString = mString;
        this.context = context;

    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.day);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView tempTV = (TextView) rowView.findViewById(R.id.temps);
        View separator = rowView.findViewById(R.id.Separator);

        txtTitle.setTextColor(Color.parseColor(colourString));

        if (getItem(position).getDayInfo() == null) {
            separator.setVisibility(View.VISIBLE);
            txtTitle.setVisibility(View.GONE);
        } else {
            txtTitle.setText(getItem(position).getDayInfo());
        }

        if (getItem(position).getImgId() == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(WeatherUtilities.getArtResourceForWeatherCondition(getItem(position).getImgId()));
        }

        if (getItem(position).getMinTempInfo() == 0.0d) {
            tempTV.setVisibility(View.GONE);
        } else {
            String tempHTML = "<font color=#cc0029>" + Integer.toString(getItem(position).getMinTempInfo()) + "</font> <font color=#0516DE>" + Integer.toString(getItem(position).getMaxTempInfo()) + "</font>";
            tempTV.setText(Html.fromHtml(tempHTML));
        }

        return rowView;

    }
}
