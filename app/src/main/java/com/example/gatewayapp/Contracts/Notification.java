package com.example.gatewayapp.Contracts;

import com.example.gatewayapp.ContractModels.NotifierResponse;
import com.example.gatewayapp.ContractModels.RequestUpdateMessage;
import com.example.gatewayapp.ContractModels.ResponseUpdateMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Notification {
    @GET("/api/notify/people")
    Call<List<NotifierResponse>> getNotify();

    @POST("/api/sms/message/done")
    Call<ResponseUpdateMessage> updateMessage(@Body RequestUpdateMessage requestUpdateMessage);
}
