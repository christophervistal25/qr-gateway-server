package com.example.gatewayapp.Database.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.gatewayapp.Database.Models.SendStatus;
import java.util.List;



@Dao
public interface SendStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void create(SendStatus sendStatus);

    @Query("SELECT * FROM send_status")
    List<SendStatus> getAll();

    @Query("SELECT * FROM send_status WHERE status != 'Send'")
    List<SendStatus> getFailedMessages();
}
