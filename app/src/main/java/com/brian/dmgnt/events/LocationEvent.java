package com.brian.dmgnt.events;

import com.brian.dmgnt.models.UserLocation;

public class LocationEvent {

    private UserLocation mUserLocation;

    public LocationEvent() {
    }

    public UserLocation getUserLocation() {
        return mUserLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        mUserLocation = userLocation;
    }
}
