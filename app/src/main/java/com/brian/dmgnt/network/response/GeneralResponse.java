package com.brian.dmgnt.network.response;

import com.brian.dmgnt.models.CovidGeneral;

public class GeneralResponse {

    private CovidGeneral mCovidGeneral;
    private Throwable mThrowable;

    public GeneralResponse(CovidGeneral covidGeneral) {
        mCovidGeneral = covidGeneral;
        mThrowable = null;
    }

    public GeneralResponse(Throwable throwable) {
        mThrowable = throwable;
        mCovidGeneral = null;
    }

    public CovidGeneral getCovidGeneral() {
        return mCovidGeneral;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }
}
