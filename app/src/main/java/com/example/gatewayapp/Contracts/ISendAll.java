package com.example.gatewayapp.Contracts;

import com.example.gatewayapp.ContractModels.RequestBulkPerson;
import com.example.gatewayapp.ContractModels.ResponsePerson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ISendAll {
    @POST("/api/bulk/person/log")
    Call<ResponsePerson> bulkPersonLog(@Body RequestBulkPerson persons);
}
