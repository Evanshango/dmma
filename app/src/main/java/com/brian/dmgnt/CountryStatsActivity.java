package com.brian.dmgnt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.viewmodels.CountryReportViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.brian.dmgnt.helpers.Constants.SHORT_DATE;

public class CountryStatsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String countryName;
    private CountryReportViewModel mCountryReportViewModel;
    private ProgressBar statsLoader;
    private TextView date;
    private BarChart mBarChart;
    private String[] xData = {"Infected", "Recovered", "Deaths"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_stats);

        mCountryReportViewModel = new ViewModelProvider(this).get(CountryReportViewModel.class);

        Intent intent = getIntent();
        if (intent != null){
            countryName  = intent.getStringExtra("countryName");
        }
        initViews();

        mToolbar.setTitle(countryName);

        getStats();
    }

    private void getStats() {
        statsLoader.setVisibility(View.VISIBLE);
        mCountryReportViewModel.getCountryReport(countryName);
        mCountryReportViewModel.getGeneralResponse().observe(this, generalResponse -> {
            if (generalResponse.getThrowable() == null){
                displayData(generalResponse.getCovidGeneral());
            } else {
                Toast.makeText(this, "Check your connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(CovidGeneral covidGeneral) {
        if (covidGeneral != null){
            statsLoader.setVisibility(View.GONE);
            DateFormat dateFormat = new SimpleDateFormat(SHORT_DATE, Locale.getDefault());
            String newDate = dateFormat.format(covidGeneral.getLastUpdate());
            date.setText(newDate);
            int infected = Integer.parseInt(covidGeneral.getConfirmed().getValue());
            int recovered = Integer.parseInt(covidGeneral.getRecovered().getValue());
            int deaths = Integer.parseInt(covidGeneral.getDeaths().getValue());

            int[] yData = {infected, recovered, deaths};
            addData(yData);
        } else {
            Toast.makeText(this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
        }
    }

    private void addData(int[] yData) {

        ArrayList<BarEntry> yEntries = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) {
            yEntries.add(new BarEntry(i, yData[i]));
        }
        BarDataSet barDataSet = new BarDataSet(yEntries, "Country Stats");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(10);

        BarData barData = new BarData(barDataSet);
        mBarChart.setFitBars(true);
        mBarChart.setData(barData);
        mBarChart.getDescription().setText("");
        mBarChart.animateY(2000);

        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value) {
                if (value >=0){
                    if (value <= xData.length - 1){
                        return xData[(int) value];
                    }
                    return "";
                }
                return "";
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarChart.invalidate();
    }

    private void initViews() {
        mToolbar = findViewById(R.id.country_stats_toolbar);
        statsLoader = findViewById(R.id.statsLoader);
        date = findViewById(R.id.date);
        mBarChart = findViewById(R.id.barChart);
    }
}
