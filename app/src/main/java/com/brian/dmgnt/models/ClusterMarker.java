package com.brian.dmgnt.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String title;
    private String snippet;
    private String iconPic;
    private Incident mIncident;

    public ClusterMarker() {
    }

    public ClusterMarker(LatLng position, String title, String snippet, String iconPic, Incident incident) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPic = iconPic;
        mIncident = incident;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getIconPic() {
        return iconPic;
    }

    public void setIconPic(String iconPic) {
        this.iconPic = iconPic;
    }

    public Incident getIncident() {
        return mIncident;
    }

    public void setIncident(Incident incident) {
        mIncident = incident;
    }
}
