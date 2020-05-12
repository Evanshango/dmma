package com.brian.dmgnt.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.network.Api;
import com.brian.dmgnt.network.ApiService;
import com.brian.dmgnt.network.response.GeneralResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryRepo {

    private Api mApi;

    public CountryRepo() {
        mApi = ApiService.getCountriesApiClient();
    }

    public LiveData<GeneralResponse> getCountryReport(String country){
        MutableLiveData<GeneralResponse> generalResponse = new MutableLiveData<>();
        mApi.getCountryReport(country).enqueue(new Callback<CovidGeneral>() {
            @Override
            public void onResponse(Call<CovidGeneral> call, Response<CovidGeneral> response) {
                if (response.isSuccessful() && response.body() != null){
                    generalResponse.postValue(new GeneralResponse(response.body()));
                } else {
                    generalResponse.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<CovidGeneral> call, Throwable t) {
                generalResponse.postValue(new GeneralResponse(t));
            }
        });
        return generalResponse;
    }
}
