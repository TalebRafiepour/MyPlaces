package com.marand.myplaces.model;

import java.util.ArrayList;

public class Response {
    private float suggestedRadius;
    private String headerLocation;
    private String headerFullLocation;
    private String headerLocationGranularity;
    private float totalResults;
    private ArrayList<Group> groups = new ArrayList <>();

    public float getSuggestedRadius() {
        return suggestedRadius;
    }

    public void setSuggestedRadius(float suggestedRadius) {
        this.suggestedRadius = suggestedRadius;
    }

    public String getHeaderLocation() {
        return headerLocation;
    }

    public void setHeaderLocation(String headerLocation) {
        this.headerLocation = headerLocation;
    }

    public String getHeaderFullLocation() {
        return headerFullLocation;
    }

    public void setHeaderFullLocation(String headerFullLocation) {
        this.headerFullLocation = headerFullLocation;
    }

    public String getHeaderLocationGranularity() {
        return headerLocationGranularity;
    }

    public void setHeaderLocationGranularity(String headerLocationGranularity) {
        this.headerLocationGranularity = headerLocationGranularity;
    }

    public float getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(float totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<Group> groups) {
        this.groups = groups;
    }
}
