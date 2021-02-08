package com.example.gatewayapp.ContractModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonIDRequest {
    @SerializedName("barangay")
    @Expose
    private String barangay;

    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }
}
