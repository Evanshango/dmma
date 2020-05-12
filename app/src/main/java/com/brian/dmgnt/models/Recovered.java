package com.brian.dmgnt.models;

import com.google.gson.annotations.SerializedName;

public class Recovered {

    @SerializedName("value")
    private String value;
    @SerializedName("detail")
    private String detail;

    public Recovered(String value, String detail) {
        this.value = value;
        this.detail = detail;
    }

    public String getValue() {
        return value;
    }

    public String getDetail() {
        return detail;
    }
}
