package com.example.utkua.weathertron;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.example.utkua.weathertron.Data.AllDayModel;

/**
 * Created by utkua on 8/7/2017.
 */

public class AllDayAdapter extends ArrayAdapter<AllDayModel> {
    public AllDayAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }
}
