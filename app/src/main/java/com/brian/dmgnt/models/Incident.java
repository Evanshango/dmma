package com.brian.dmgnt.models;

import com.google.firebase.firestore.GeoPoint;

public class Incident {

    private String incidentId, category, imageUrl, description, userId, date, time;
    private GeoPoint mGeoPoint;

    public Incident() {
    }

    public Incident(String incidentId, String category, String imageUrl, String description,
                    String userId, GeoPoint geoPoint, String date, String time) {
        this.incidentId = incidentId;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.userId = userId;
        mGeoPoint = geoPoint;
        this.date = date;
        this.time = time;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        mGeoPoint = geoPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
