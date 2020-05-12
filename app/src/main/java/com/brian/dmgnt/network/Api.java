package com.brian.dmgnt.network;

import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.network.response.CountryResponse;
import com.brian.dmgnt.network.response.GeneralResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    @GET("api")
    Call<CovidGeneral> getDailyReport();

    @GET("countries")
    Call<CountryResponse> getCountries();
}
