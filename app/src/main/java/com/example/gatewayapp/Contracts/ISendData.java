package com.example.gatewayapp.Contracts;

import com.example.gatewayapp.ContractModels.SendDataRequest;
import com.example.gatewayapp.ContractModels.SendDataResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISendData {
    @POST("/api/person/scanned")
    Call<SendDataResponse> sendPersonLog(@Body SendDataRequest user);
}
