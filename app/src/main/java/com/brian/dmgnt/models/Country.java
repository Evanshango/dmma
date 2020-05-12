package com.brian.dmgnt.models;

import com.google.gson.annotations.SerializedName;

public class Country {

    @SerializedName("name")
    private String name;
    @SerializedName("iso3")
    private String iso;

    public Country(String name, String iso) {
        this.name = name;
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public String getIso() {
        return iso;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", iso='" + iso + '\'' +
                '}';
    }
}
