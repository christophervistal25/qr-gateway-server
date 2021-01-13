package com.example.gatewayapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendDataRequest {
    @SerializedName("person_id")
    @Expose
    private String user_id;

    @SerializedName("temperature")
    @Expose
    private String temperature;


    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("time")
    @Expose
    private String time;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
