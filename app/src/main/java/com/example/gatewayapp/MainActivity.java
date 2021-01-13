package com.example.gatewayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gatewayapp.Adapters.SendStatusAdapter;
import com.example.gatewayapp.Database.DB;
import com.example.gatewayapp.Database.Models.SendStatus;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SendStatusAdapter.ItemClickListener {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    SendStatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.permissionForAccessingSMS();
        //insertFakeData();
        initRecyclerView();
        Button btnFailedMessage = findViewById(R.id.btnFailedMessages);
        Button btnResendAll = findViewById(R.id.btnReSendAll);

        List<SendStatus> noOfFailedMessages = DB.getInstance(this).sendStatusDao().getFailedMessages();

        btnFailedMessage.setText(String.format("FAILED MESSAGES > %d", noOfFailedMessages.size()));


        btnResendAll.setOnClickListener(v -> {
            Toast.makeText(this, "Make a API Request", Toast.LENGTH_SHORT).show();
        });


    }

    private void insertFakeData() {
        for(int i = 0; i<20; i++) {
            SendStatus sendStatus = new SendStatus();
            sendStatus.setData_message("Sample " + i);
            sendStatus.setStatus("Failed");
            DB.getInstance(getApplicationContext()).sendStatusDao().create(sendStatus);
        }
    }

    private void initRecyclerView() {
        List<SendStatus> personLogList = DB.getInstance(this).sendStatusDao().getAll();

        RecyclerView recyclerView = findViewById(R.id.send_list_status);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SendStatusAdapter(this, personLogList);
        adapter.setClickListener(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xfff7f7f7, 0xfff7f7f7});
        drawable.setSize(1,2);
        itemDecoration.setDrawable(drawable);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
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