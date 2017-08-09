package com.example.utkua.weathertron;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.utkua.weathertron.R;

public class ChooseCityActivity extends AppCompatActivity {

    EditText mInputET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

        FloatingActionButton floaterButt = (FloatingActionButton) findViewById(R.id.city_choose_FAB);
        mInputET = (EditText) findViewById(R.id.choose_city_ET);

        floaterButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set inputLocation to the location which was input by the user
                String locationString = mInputET.getText().toString();
                Intent intent = new Intent(ChooseCityActivity.this, ChosenCityActivity.class);
                intent.putExtra("location", locationString);
                startActivity(intent);
            }
        });
    }
}
