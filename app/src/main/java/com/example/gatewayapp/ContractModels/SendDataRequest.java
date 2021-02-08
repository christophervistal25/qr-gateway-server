package com.example.gatewayapp.ContractModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendDataRequest {
    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("temperature")
    @Expose
    private String temperature;


    @SerializedName("checker_id")
    @Expose
    private String checker_id;



    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("purpose")
    @Expose
    private String purpose;

    @SerializedName("time")
    @Expose
    private String time;

    public void setChecker_id(String checker_id) {
        this.checker_id = checker_id;
    }

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

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
