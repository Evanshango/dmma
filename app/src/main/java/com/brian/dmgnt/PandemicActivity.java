package com.brian.dmgnt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.brian.dmgnt.dialogs.CountryDialog;
import com.brian.dmgnt.models.Country;
import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.viewmodels.PandemicViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static com.brian.dmgnt.helpers.Constants.SHORT_DATE;

public class PandemicActivity extends AppCompatActivity implements CountryDialog.ItemClick {

    private PandemicViewModel mPandemicViewModel;
    private ProgressBar loader;
    private TextView txtInfected, infDate, txtRecovered, recDate, txtDeaths, deathsDate;
    private PieChart mPieChart;
    private Button btnViewCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandemic);

        initViews();

        mPandemicViewModel = new ViewModelProvider(this).get(PandemicViewModel.class);

        getGeneralInfo();

        btnViewCountry.setOnClickListener(v -> toViewCountrySpecific());
    }

    private void toViewCountrySpecific() {
        CountryDialog countryDialog = new CountryDialog();
        countryDialog.show(getSupportFragmentManager(), "countryDialog");
    }

    private void getGeneralInfo() {
        loader.setVisibility(View.VISIBLE);
        mPandemicViewModel.getGeneral();
        mPandemicViewModel.getGeneralResponse().observe(this, generalResponse -> {
            if (generalResponse.getThrowable() == null) {
                displayData(generalResponse.getCovidGeneral());
            } else {
                Toast.makeText(this, "Check Your Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData(CovidGeneral covidGeneral) {
        if (covidGeneral != null) {
            loader.setVisibility(View.GONE);
            DateFormat dateFormat = new SimpleDateFormat(SHORT_DATE, Locale.getDefault());
            String newDate = dateFormat.format(covidGeneral.getLastUpdate());
            int infected = Integer.parseInt(covidGeneral.getConfirmed().getValue());
            int recovered = Integer.parseInt(covidGeneral.getRecovered().getValue());
            int deaths = Integer.parseInt(covidGeneral.getDeaths().getValue());

            txtInfected.setText(String.valueOf(infected));
            txtRecovered.setText(String.valueOf(recovered));
            txtDeaths.setText(String.valueOf(deaths));
            infDate.setText(newDate);
            recDate.setText(newDate);
            deathsDate.setText(newDate);

            int[] yData = {infected, recovered, deaths};
            String[] xData = {"Infected", "Recovered", "Deaths"};

            mPieChart.getDescription().setText("");
            mPieChart.setHoleRadius(30f);
            mPieChart.setTransparentCircleAlpha(0);
            mPieChart.setCenterText("Global Numbers");
            mPieChart.setCenterTextSize(10);
            mPieChart.setDrawEntryLabels(true);

            addData(yData);

            mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    float number = e.getY();
                    String label = null;
                    for (int i = 0; i < yData.length; i++) {
                        if (yData[i] == number) {
                            number = yData[i];
                            label = xData[i];
                            break;
                        }
                    }
                    Toast.makeText(PandemicActivity.this,
                            label + " " + (int) number,
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });

        } else {
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    private void addData(int[] yData) {
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) {
            yEntries.add(new PieEntry(yData[i], i));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "Total Numbers");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(10);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        pieDataSet.setColors(colors);

        Legend legend = mPieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);

        PieData pieData = new PieData(pieDataSet);
        mPieChart.setData(pieData);
        mPieChart.invalidate();
    }

    private void initViews() {
        loader = findViewById(R.id.loader);
        txtInfected = findViewById(R.id.txtInfected);
        infDate = findViewById(R.id.txtInfDate);
        txtRecovered = findViewById(R.id.txtRecovered);
        recDate = findViewById(R.id.txtRecDate);
        txtDeaths = findViewById(R.id.txtDeaths);
        deathsDate = findViewById(R.id.txtDeathsDate);
        mPieChart = findViewById(R.id.globalPieChart);
        btnViewCountry = findViewById(R.id.btnViewCountry);
    }

    @Override
    public void itemInteraction(Country country) {
        Intent intent = new Intent(this, CountryStatsActivity.class);
        intent.putExtra("countryName", country.getName());
        startActivity(intent);
    }
}
