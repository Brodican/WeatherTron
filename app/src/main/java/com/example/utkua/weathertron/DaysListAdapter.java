package com.example.utkua.weathertron;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.utkua.weathertron.Utilities.WeatherUtilities;

/**
 * Created by utkua on 8/7/2017.
 */

public class DaysListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public static final String TAG = "DaysListActivity";

    public DaysListAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.mylist, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.mylist, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.day);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.temps);

        txtTitle.setText(itemname[position]);
        Log.d(TAG, "img id in DaysListAdapter: " + imgid[position]);
        imageView.setImageResource(WeatherUtilities.getIconResourceForWeatherCondition(imgid[position]));
        extratxt.setText("Description "+itemname[position]);
        return rowView;

    }
}
