package com.brian.dmgnt.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.brian.dmgnt.models.Country;
import com.brian.dmgnt.network.Api;
import com.brian.dmgnt.network.ApiService;
import com.brian.dmgnt.network.response.CountryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportRepo {

    private Api mApi;

    public ReportRepo() {
        mApi = ApiService.getCountriesApiClient();
    }

    public LiveData<CountryResponse> getCountries(){
        MutableLiveData<CountryResponse> countryResponse = new MutableLiveData<>();
        mApi.getCountries().enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    countryResponse.postValue(new CountryResponse(response.body().getCountries()));
                } else {
                    countryResponse.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                countryResponse.postValue(new CountryResponse(t));
            }
        });
        return countryResponse;
    }
}
