package com.example.gatewayapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISendData {
    @POST("person/scanned/sms")
    Call<SendDataResponse> sendPersonLog(@Body SendDataRequest user);
}
