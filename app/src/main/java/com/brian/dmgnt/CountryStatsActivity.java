package com.brian.dmgnt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

public class CountryStatsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_stats);
        Intent intent = getIntent();
        if (intent != null){
            countryName  = intent.getStringExtra("countryName");
        }
        initViews();

        mToolbar.setTitle(countryName);
    }

    private void initViews() {
        mToolbar = findViewById(R.id.country_stats_toolbar);
    }
}
