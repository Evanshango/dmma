package com.brian.dmgnt.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {

    private GeoPoint mGeoPoint;
    private @ServerTimestamp Date timestamp;
    private User mUser;

    public UserLocation() {
    }

    public UserLocation(GeoPoint geoPoint, Date timestamp, User user) {
        mGeoPoint = geoPoint;
        this.timestamp = timestamp;
        mUser = user;
    }

    public GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        mGeoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
