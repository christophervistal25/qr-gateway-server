package com.example.gatewayapp.Contracts;

import com.example.gatewayapp.ContractModels.PersonIDRequest;
import com.example.gatewayapp.ContractModels.ResponsePerson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IPersonID {
    @POST("/api/person/id/generate")
    Call<ResponsePerson> generate(@Body PersonIDRequest personIDRequest);
}
