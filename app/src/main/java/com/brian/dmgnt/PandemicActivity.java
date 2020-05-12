package com.brian.dmgnt;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandemic);

        initViews();

        mPandemicViewModel = new ViewModelProvider(this).get(PandemicViewModel.class);

        mCountryAdapter = new CountryAdapter(this);

        getGeneralInfo();

        getCountries();
    }

    private void getGeneralInfo() {
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
            DateFormat dateFormat = new SimpleDateFormat(SHORT_DATE, Locale.getDefault());
            String newDate = dateFormat.format(covidGeneral.getLastUpdate());

            txtInfected.setText(covidGeneral.getConfirmed().getValue());
            txtRecovered.setText(covidGeneral.getRecovered().getValue());
            txtDeaths.setText(covidGeneral.getDeaths().getValue());
            infDate.setText(newDate);
            recDate.setText(newDate);
            deathsDate.setText(newDate);
        } else {
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
        }
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
    }

    private void getCountries() {
        loader.setVisibility(View.VISIBLE);
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
