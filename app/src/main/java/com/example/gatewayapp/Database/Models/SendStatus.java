package com.example.gatewayapp.Database.Models;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName ="send_status")
public class SendStatus {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String data_message;

    public String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData_message() {
        return data_message;
    }

    public void setData_message(String data_message) {
        this.data_message = data_message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
