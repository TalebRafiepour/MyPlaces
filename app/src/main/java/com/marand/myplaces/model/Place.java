package com.marand.myplaces.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Place {
    private int id;
    @SerializedName("response")
    @Expose
    private Response response;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
