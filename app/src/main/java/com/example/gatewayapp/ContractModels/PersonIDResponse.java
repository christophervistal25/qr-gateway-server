package com.example.gatewayapp.ContractModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonIDResponse {
    @SerializedName("code")
    @Expose
    public String code;

    @SerializedName("message")
    @Expose
    public String message;

    @SerializedName("person_id")
    @Expose
    public String person_id;

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }
}
