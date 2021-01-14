package com.example.gatewayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gatewayapp.Adapters.SendStatusAdapter;
import com.example.gatewayapp.Callbacks.ReceiverCallback;
import com.example.gatewayapp.Database.DB;
import com.example.gatewayapp.Database.Models.PersonLog;
import com.example.gatewayapp.Database.Models.SendStatus;
import com.example.gatewayapp.Helpers.ASCIIToChar;
import com.example.gatewayapp.SMS.SMSReceiver;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements SendStatusAdapter.ItemClickListener {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    // 124 in character is "|"
    private static final int DATA_SEPARATOR = 124;
     SendStatusAdapter adapter;
     List<SendStatus> sendStatusList;
     Button btnFailedMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.permissionForAccessingSMS();
//        this.insertFakeData();
        initRecyclerView();
        btnFailedMessage = findViewById(R.id.btnFailedMessages);
        Button btnResendAll = findViewById(R.id.btnReSendAll);

        List<SendStatus> noOfFailedMessages = DB.getInstance(this).sendStatusDao().getFailedMessages();

        btnFailedMessage.setText(String.format("FAILED MESSAGES > %d", noOfFailedMessages.size()));


        List<PersonLog> personLogList = new ArrayList<>();

        btnResendAll.setOnClickListener(v -> {
            List<SendStatus> failedMessages = DB.getInstance(getApplicationContext()).sendStatusDao().getFailedMessages();
            for(SendStatus record : failedMessages) {
                List<String> information = ASCIIToChar.convert(record.getData_message());
                Log.d("USER_", information.get(0) + " " + information.get(1) + " " + information.get(2) + " " + information.get(3));

                PersonLog personLog = new PersonLog();
                personLog.setPerson_id(information.get(0));
                personLog.setLocation(information.get(1));
                personLog.setBody_temperature(information.get(2));
                personLog.setTime(information.get(3));

                personLogList.add(personLog);
            }

            Retrofit retrofit = RetrofitService.RetrofitInstance(getApplicationContext());
            SendAll service = retrofit.create(SendAll.class);
            String jsonPersonLogList = new Gson().toJson(personLogList);

            RequestBulkPerson requestBulkPerson = new RequestBulkPerson();
            requestBulkPerson.setData(jsonPersonLogList);

            Log.d("USER_DATA_RESULT", jsonPersonLogList);

            Call<ResponsePerson> responsePersonCall = service.bulkPersonLog(requestBulkPerson);
            responsePersonCall.enqueue(new Callback<ResponsePerson>() {
                @Override
                public void onResponse(Call<ResponsePerson> call, Response<ResponsePerson> response) {
                    //
                    for(SendStatus record : failedMessages) {
                        DB.getInstance(getApplicationContext()).sendStatusDao().update(record.getId());
                    }
                    initRecyclerView();
                    Toast.makeText(MainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponsePerson> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



        });

    }





    private void insertFakeData() {
        for(int i = 0; i<20; i++) {
            SendStatus sendStatus = new SendStatus();
            sendStatus.setData_message("50:124:84:97:110:100:97:103:32:67:105:116:121:124:51:54:46:55:124:74:97:110:32:49:52:44:32:50:48:50:49:32:49:50:58:51:57:58:48:49:32:80:77:");
            sendStatus.setStatus("Failed");
            DB.getInstance(getApplicationContext()).sendStatusDao().create(sendStatus);
        }
    }

    private void initRecyclerView() {
        sendStatusList = DB.getInstance(this).sendStatusDao().getFailedMessages();
        RecyclerView recyclerView = findViewById(R.id.send_list_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SendStatusAdapter(this, sendStatusList);
        adapter.setClickListener(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xfff7f7f7, 0xfff7f7f7});
        drawable.setSize(1,2);
        itemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        List<SendStatus> noOfFailedMessages = DB.getInstance(this).sendStatusDao().getFailedMessages();

        btnFailedMessage.setText(String.format("FAILED MESSAGES > %d", noOfFailedMessages.size()));
        initRecyclerView();
        super.onResume();
    }

    private void permissionForAccessingSMS()
    {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, MY_PERMISSIONS_REQUEST_SEND_SMS);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        // If the permission deny display the dialog again.
        if(!String.valueOf(grantResults[0]).equals("0")) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

    }


    @Override
    public void onItemClick(View view, int position) {
        // Record with failed status
        if(!adapter.getItem(position).getStatus().equals("Send")) {
            SendStatus record = adapter.getItem(position);
            AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
            confirmationDialog.setTitle("Re-send data");
            confirmationDialog.setMessage(String.format("Do you want to re-send this record with ID : %s and status : %s", record.getId(), record.getStatus()));
            confirmationDialog.setCancelable(true);

            confirmationDialog.setPositiveButton(
                    "Re-send ",
                    (dialog, id) -> Toast.makeText(this, "Re-send this data.", Toast.LENGTH_SHORT).show());

            confirmationDialog.setNegativeButton(
                    "No",
                    (dialog, id) -> {
                        dialog.cancel();
                    });

            AlertDialog resendDialog = confirmationDialog.create();
            resendDialog.show();
        }
    }



}