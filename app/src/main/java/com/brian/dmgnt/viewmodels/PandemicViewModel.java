package com.brian.dmgnt.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.brian.dmgnt.network.response.CountryResponse;
import com.brian.dmgnt.network.response.GeneralResponse;
import com.brian.dmgnt.repository.GeneralRepo;
import com.brian.dmgnt.repository.ReportRepo;

public class PandemicViewModel extends AndroidViewModel {

    private ReportRepo mReportRepo;
    private GeneralRepo mGeneralRepo;
    private LiveData<CountryResponse> mCountryResponse;
    private LiveData<GeneralResponse> mGeneralResponse;

    public PandemicViewModel(@NonNull Application application) {
        super(application);
        mReportRepo = new ReportRepo();
        mGeneralRepo = new GeneralRepo();
    }

    public void getCountries(){
        if (mCountryResponse != null){
            return;
        }
        mCountryResponse = mReportRepo.getCountries();
    }

    public void getGeneral(){
        if (mGeneralResponse != null){
            return;
        }
        mGeneralResponse = mGeneralRepo.getGeneralReport();
    }

    public LiveData<CountryResponse> getCountryResponse() {
        return mCountryResponse;
    }

    public LiveData<GeneralResponse> getGeneralResponse(){ return mGeneralResponse;}
}
