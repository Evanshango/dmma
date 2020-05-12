package com.brian.dmgnt;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.dmgnt.adapters.CountryAdapter;
import com.brian.dmgnt.models.Country;
import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.viewmodels.PandemicViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.brian.dmgnt.helpers.Constants.SHORT_DATE;

public class PandemicActivity extends AppCompatActivity implements CountryAdapter.ItemInteraction {

    private static final String TAG = "PandemicActivity";
    private PandemicViewModel mPandemicViewModel;
    private CountryAdapter mCountryAdapter;
    private ProgressBar loader;
    private List<Country> mCountries = new ArrayList<>();
    private RecyclerView countryRecycler;
    private TextView txtInfected, infDate, txtRecovered, recDate, txtDeaths, deathsDate;
    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandemic);

        initViews();

        mPandemicViewModel = new ViewModelProvider(this).get(PandemicViewModel.class);

        mCountryAdapter = new CountryAdapter(this);

        getGeneralInfo();

//        getCountries();
    }

    private void getGeneralInfo() {
        loader.setVisibility(View.VISIBLE);
        mPandemicViewModel.getGeneral();
        mPandemicViewModel.getGeneralResponse().observe(this, generalResponse -> {
            if (generalResponse.getThrowable() == null) {
                displayData(generalResponse.getCovidGeneral());
            } else {
                Log.d(TAG, "getGeneralInfo: Something Went Wrong");
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

            mPieChart.getDescription().setText("Total Global Numbers");
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
        countryRecycler = findViewById(R.id.countryRecycler);
        txtInfected = findViewById(R.id.txtInfected);
        infDate = findViewById(R.id.txtInfDate);
        txtRecovered = findViewById(R.id.txtRecovered);
        recDate = findViewById(R.id.txtRecDate);
        txtDeaths = findViewById(R.id.txtDeaths);
        deathsDate = findViewById(R.id.txtDeathsDate);
        mPieChart = findViewById(R.id.globalPieChart);
    }

    private void getCountries() {
        mPandemicViewModel.getCountries();
        mPandemicViewModel.getCountryResponse().observe(this, countryResponse -> {
            if (countryResponse.getThrowable() == null) {
                loader.setVisibility(View.GONE);
                prepareCountries(countryResponse.getCountries());
            } else {
                loader.setVisibility(View.GONE);
                Toast.makeText(this, "Please check you connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareCountries(List<Country> countries) {
        if (countries != null) {
            mCountries.clear();
            mCountries.addAll(countries);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            countryRecycler.setHasFixedSize(true);
            countryRecycler.setLayoutManager(manager);

            mCountryAdapter.setCountries(mCountries);
            countryRecycler.setAdapter(mCountryAdapter);
        } else {
            Toast.makeText(this, "No Countries found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void countryClick(Country country) {
        Toast.makeText(this, "opening dialog", Toast.LENGTH_SHORT).show();
    }
}
