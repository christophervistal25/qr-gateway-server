package com.example.gatewayapp.Callbacks;

import com.example.gatewayapp.Database.Models.SendStatus;

public interface ReceiverCallback {
    void updateUIDisplayNewData(SendStatus data);
}
