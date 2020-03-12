package com.brian.dmgnt.models;

import com.google.firebase.firestore.GeoPoint;

public class Incident {

    private String incidentId, category, imageUrl, description, userId, timestamp;
    private GeoPoint mGeoPoint;

    public Incident() {
    }

    public Incident(String incidentId, String category, String imageUrl, String description,
                    String userId, GeoPoint geoPoint, String timestamp) {
        this.incidentId = incidentId;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
        this.userId = userId;
        mGeoPoint = geoPoint;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
