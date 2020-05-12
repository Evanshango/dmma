package com.brian.dmgnt.network;

import com.brian.dmgnt.models.CovidGeneral;
import com.brian.dmgnt.network.response.CountryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Api {

    @GET("api")
    Call<CovidGeneral> getDailyReport();

    @GET("countries")
    Call<CountryResponse> getCountries();

    @GET("countries/{country}")
    Call<CovidGeneral> getCountryReport(@Path("country") String country);
}
