package com.example.gatewayapp.Contracts;

import com.example.gatewayapp.ContractModels.NotifierResponse;
import com.example.gatewayapp.ContractModels.SendDataRequest;
import com.example.gatewayapp.ContractModels.SendDataResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface Notification {
    @GET("/test/notify")
    Call<List<NotifierResponse>> getNotify();
}
