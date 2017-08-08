package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.utkua.weathertron.Data.AllDayModel;
import com.example.utkua.weathertron.Data.WeatherModel;

import java.util.List;

/**
 * Created by utkua on 8/7/2017.
 */

public class AllDayAdapter extends RecyclerView.Adapter<AllDayModel> {

    private List<AllDayModel> items;
    private int itemLayout;


    public AllDayAdapter(List<AllDayModel> items, int itemLayout) {
        this.items = items;
        this.itemLayout = itemLayout;

    }

    @Override
    public AllDayModel onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new AllDayModel(v);

    }

    @Override
    public void onBindViewHolder(AllDayModel holder, int position) {
        AllDayModel item = items.get(position);
        holder.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
