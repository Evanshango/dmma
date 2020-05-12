package com.brian.dmgnt.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brian.dmgnt.network.response.GeneralResponse;
import com.brian.dmgnt.repository.CountryRepo;

public class CountryReportViewModel extends AndroidViewModel {

    private CountryRepo mCountryRepo;
    private LiveData<GeneralResponse> mGeneralResponse;

    public CountryReportViewModel(@NonNull Application application) {
        super(application);
        mCountryRepo = new CountryRepo();
    }

    public void getCountryReport(String country){
        if (mGeneralResponse != null){
            return;
        }
        mGeneralResponse = mCountryRepo.getCountryReport(country);
    }

    public LiveData<GeneralResponse> getGeneralResponse(){ return mGeneralResponse;}
}
