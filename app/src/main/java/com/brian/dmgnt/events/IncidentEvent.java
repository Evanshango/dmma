package com.brian.dmgnt.events;

import com.brian.dmgnt.models.Incident;

import java.util.List;

public class IncidentEvent {

    private List<Incident> mIncidents;

    public IncidentEvent() {
    }

    public List<Incident> getIncidents() {
        return mIncidents;
    }

    public void setIncidents(List<Incident> incidents) {
        mIncidents = incidents;
    }
}
