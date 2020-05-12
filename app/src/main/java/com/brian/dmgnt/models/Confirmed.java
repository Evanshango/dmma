package com.brian.dmgnt.models;

import com.google.gson.annotations.SerializedName;

public class Confirmed {

    @SerializedName("value")
    private String value;
    @SerializedName("detail")
    private String detail;

    public Confirmed(String value, String detail) {
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
