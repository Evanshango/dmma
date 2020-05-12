package com.brian.dmgnt.models;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class CovidGeneral {

    @SerializedName("confirmed")
    private Confirmed mConfirmed;

    @SerializedName("recovered")
    private Recovered mRecovered;

    @SerializedName("deaths")
    private Deaths mDeaths;

    @SerializedName("lastUpdate")
    private Timestamp lastUpdate;

    public CovidGeneral(Confirmed confirmed, Recovered recovered, Deaths deaths, Timestamp lastUpdate) {
        mConfirmed = confirmed;
        mRecovered = recovered;
        mDeaths = deaths;
        this.lastUpdate = lastUpdate;
    }

    public Confirmed getConfirmed() {
        return mConfirmed;
    }

    public Recovered getRecovered() {
        return mRecovered;
    }

    public Deaths getDeaths() {
        return mDeaths;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }
}
