package com.example.gatewayapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.gatewayapp.Database.Daos.SendStatusDao;
import com.example.gatewayapp.Database.Models.SendStatus;



@Database(entities = {SendStatus.class},version = 1)
public abstract class DB extends RoomDatabase {

    private static DB appDatabase;
    private Context context;
    public abstract SendStatusDao sendStatusDao();

    public synchronized  static DB getInstance(Context context){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(context.getApplicationContext(), DB.class, "server_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return appDatabase;
    }

    public void destroyInstance() {
        appDatabase = null;
    }
}