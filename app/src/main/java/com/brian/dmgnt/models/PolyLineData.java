package com.brian.dmgnt.models;

import com.google.android.gms.maps.model.Polyline;
import com.google.maps.model.DirectionsLeg;

public class PolyLineData {

    private Polyline mPolyline;
    private DirectionsLeg mDirectionsLeg;

    public PolyLineData(Polyline polyline, DirectionsLeg directionsLeg) {
        mPolyline = polyline;
        mDirectionsLeg = directionsLeg;
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    public void setPolyline(Polyline polyline) {
        mPolyline = polyline;
    }

    public DirectionsLeg getDirectionsLeg() {
        return mDirectionsLeg;
    }

    public void setDirectionsLeg(DirectionsLeg directionsLeg) {
        mDirectionsLeg = directionsLeg;
    }

    @Override
    public String toString() {
        return "PolyLineData{" +
                "mPolyline=" + mPolyline +
                ", mDirectionsLeg=" + mDirectionsLeg +
                '}';
    }
}
