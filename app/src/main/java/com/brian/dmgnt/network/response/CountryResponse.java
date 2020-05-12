package com.brian.dmgnt.network.response;

import com.brian.dmgnt.models.Country;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryResponse {

    @SerializedName("countries")
    private List<Country> mCountries;

    private Throwable mThrowable;

    public CountryResponse(List<Country> countries) {
        mCountries = countries;
        mThrowable = null;
    }

    public CountryResponse(Throwable throwable) {
        mThrowable = throwable;
        mCountries = null;
    }

    public List<Country> getCountries() {
        return mCountries;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
