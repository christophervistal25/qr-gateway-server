package com.example.gatewayapp.ContractModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonIDRequest {
    @SerializedName("barangay")
    @Expose
    private String barangay;

    @SerializedName("firstname")
    @Expose
    private String firstname;

    @SerializedName("lastname")
    @Expose
    private String lastname;

    @SerializedName("middlename")
    @Expose
    private String middlename;

    @SerializedName("suffix")
    @Expose
    private String suffix;

    @SerializedName("date_of_birth")
    @Expose
    private String date_of_birth;

    @SerializedName("phone_number")
    @Expose
    private String phone_number;


    public String getBarangay() {
        return barangay;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
