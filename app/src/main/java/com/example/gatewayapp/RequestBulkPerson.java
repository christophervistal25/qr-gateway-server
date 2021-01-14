package com.example.gatewayapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestBulkPerson {
    @SerializedName("data")
    @Expose
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
