package com.example.gatewayapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SendAll {
    @POST("/api/bulk/person/log")
    Call<ResponsePerson> bulkPersonLog(@Body RequestBulkPerson persons);
}
