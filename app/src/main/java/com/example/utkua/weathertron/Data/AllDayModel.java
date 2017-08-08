package com.example.utkua.weathertron.Data;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by utkua on 8/7/2017.
 */

public class AllDayModel extends RecyclerView.ViewHolder {

    String text;

    public AllDayModel(View itemView) {
        super(itemView);
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
