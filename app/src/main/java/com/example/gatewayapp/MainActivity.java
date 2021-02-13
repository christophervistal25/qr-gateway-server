package com.example.gatewayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gatewayapp.Adapters.SendStatusAdapter;
import com.example.gatewayapp.ContractModels.NotifierResponse;
import com.example.gatewayapp.ContractModels.PersonIDRequest;
import com.example.gatewayapp.ContractModels.PersonIDResponse;
import com.example.gatewayapp.ContractModels.RequestBulkPerson;
import com.example.gatewayapp.ContractModels.ResponsePerson;
import com.example.gatewayapp.Contracts.IPersonID;
import com.example.gatewayapp.Contracts.Notification;
import com.example.gatewayapp.Database.DB;
import com.example.gatewayapp.Database.Models.PersonLog;
import com.example.gatewayapp.Database.Models.SendStatus;
import com.example.gatewayapp.Helpers.ASCIIToChar;
import com.example.gatewayapp.Helpers.PinGenerator;
import com.example.gatewayapp.SMS.MessageListener;
import com.example.gatewayapp.SMS.SMSReceiver;
import com.example.gatewayapp.Contracts.ISendAll;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SendStatusAdapter.ItemClickListener , MessageListener , EasyPermissions.PermissionCallbacks  {



    public static final String GATEWAY_NUMBER = "09431364951";
    public static final String MESSAGE_SEPARATOR = "z";
    public static final String REQUEST_CODE = "88f9e51be6703354608f99efbcfedf20";


    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    // 124 in character is "|"
    private static final int DATA_SEPARATOR = 124;
    private static final int PERSON_ID = 1;
     SendStatusAdapter adapter;
     List<SendStatus> sendStatusList;
     Button btnFailedMessage;
     ProgressDialog progressdialog;



    Notification apiService;
    Retrofit retrofit;
    Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SMSReceiver.bindListener(this);

        askForPermissions();

        this.requestOTPAndPersonId();


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(Notification.class);


        disposable = Observable.interval(5000, 5000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callMessageEndpoint, this::onError);



        initRecyclerView();
        btnFailedMessage = findViewById(R.id.btnFailedMessages);
        Button btnResendAll = findViewById(R.id.btnReSendAll);

        List<SendStatus> noOfFailedMessages = DB.getInstance(this).sendStatusDao().getFailedMessages();

        btnFailedMessage.setText(String.format("FAILED MESSAGES > %d", noOfFailedMessages.size()));


        List<PersonLog> personLogList = new ArrayList<>();

        btnResendAll.setOnClickListener(v -> {
            requestOTPAndPersonId();
//            progressdialog = new ProgressDialog(MainActivity.this);
//            progressdialog.setMessage("Processing please wait...");
//            progressdialog.setCancelable(false);
//            progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressdialog.show();
//
//            List<SendStatus> failedMessages = DB.getInstance(getApplicationContext()).sendStatusDao().getFailedMessages();
//            for(SendStatus record : failedMessages) {
//                List<String> information = ASCIIToChar.convert(record.getData_message());
//
//                PersonLog personLog = new PersonLog();
//                personLog.setPerson_id(information.get(0));
//                personLog.setChecker_id(information.get(1));
//                personLog.setLocation(information.get(2));
//                personLog.setBody_temperature(information.get(3));
//                personLog.setPurpose(information.get(4));
//                personLog.setTime(information.get(5));
//
//                personLogList.add(personLog);
//            }
//
//            Retrofit retrofit = RetrofitService.RetrofitInstance(getApplicationContext());
//            ISendAll service = retrofit.create(ISendAll.class);
//            String jsonPersonLogList = new Gson().toJson(personLogList);
//
//            RequestBulkPerson requestBulkPerson = new RequestBulkPerson();
//            requestBulkPerson.setData(jsonPersonLogList);
//
//            Log.d("USER_DATA_RESULT", jsonPersonLogList);
//
//            Call<ResponsePerson> responsePersonCall = service.bulkPersonLog(requestBulkPerson);
//            responsePersonCall.enqueue(new Callback<ResponsePerson>() {
//                @Override
//                public void onResponse(Call<ResponsePerson> call, Response<ResponsePerson> response) {
//                    //
//                    for(SendStatus record : failedMessages) {
//                        DB.getInstance(getApplicationContext()).sendStatusDao().update(record.getId());
//                    }
//                    initRecyclerView();
//                    progressdialog.dismiss();
//                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
//                    confirmationDialog.setTitle("All Right!");
//                    confirmationDialog.setMessage("All data successfully send.");
//                    confirmationDialog.setCancelable(true);
//                    AlertDialog confirmationAlert = confirmationDialog.create();
//                    confirmationAlert.show();
//
//                }
//
//                @Override
//                public void onFailure(Call<ResponsePerson> call, Throwable t) {
//                    progressdialog.dismiss();
//                    Toast.makeText(MainActivity.this, "Please contact the tech support something went wrong.", Toast.LENGTH_SHORT).show();
//                }
//            });

        });
    }


    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
    }

    private void callMessageEndpoint(Long aLong) {

        Call<List<NotifierResponse>> observable = apiService.getNotify();
        observable.enqueue(new Callback<List<NotifierResponse>>() {
            @Override
            public void onResponse(Call<List<NotifierResponse>> call, Response<List<NotifierResponse>> response) {
//                Toast.makeText(MainActivity.this, "Fetched data from API", Toast.LENGTH_SHORT).show();
                for(NotifierResponse notifierResponse: response.body()) {
                    PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED"), 0);
                    SmsManager.getDefault().sendTextMessage(notifierResponse.getPhoneNumber(), null, notifierResponse.getMessage(), sentPI, deliveredPI);
                }

            }

            @Override
            public void onFailure(Call<List<NotifierResponse>> call, Throwable t) {

            }
        });




    }


    public void requestOTPAndPersonId()
    {
        Retrofit retrofit2 = RetrofitService.RetrofitInstance(getApplicationContext());
        IPersonID service2 = retrofit2.create(IPersonID.class);
        PersonIDRequest personIDRequest = new PersonIDRequest();
        personIDRequest.setBarangay("166819001");
        personIDRequest.setFirstname("christopher");
        personIDRequest.setMiddlename("platino");
        personIDRequest.setLastname("vistal");
        personIDRequest.setSuffix("");
        personIDRequest.setDate_of_birth("2013-03-15");

        Call<PersonIDResponse> responseCall = service2.generate(personIDRequest);
        responseCall.enqueue(new Callback<PersonIDResponse>() {
            @Override
            public void onResponse(Call<PersonIDResponse> call, Response<PersonIDResponse> response) {
                PersonIDResponse personIDResponse = response.body();
                if(response.isSuccessful() && personIDResponse.getCode().equals("200")) {

                    String[] personInformation = personIDResponse.getPerson_id().split("-");
                    String personID = personInformation[PERSON_ID];
                    PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_SENT"), 0);
                    PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("SMS_DELIVERED"), 0);
                    // Send sms with OTP CODE.
                    SmsManager.getDefault().sendTextMessage("+639630711082", null, "Your One-Time-Pin\n" + PinGenerator.generate() + "\n#Ref Code : " + personID, sentPI, deliveredPI);
                }
            }

            @Override
            public void onFailure(Call<PersonIDResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void messageReceived(String sender, String message) {
        String[] split = message.split(MESSAGE_SEPARATOR);
        if(split[0].equals(REQUEST_CODE)) {
            Toast.makeText(this, "New user register", Toast.LENGTH_SHORT).show();
//            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
//            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
//            SmsManager.getDefault().sendTextMessage(sender, null, "Your One-Time-Pin " + PinGenerator.generate() + " please do not share this with anyone. ", sentPI, deliveredPI);
//            Retrofit retrofit2 = RetrofitService.RetrofitInstance(getApplicationContext());
//            IPersonID service2 = retrofit2.create(IPersonID.class);
//            PersonIDRequest personIDRequest = new PersonIDRequest();
//            personIDRequest.setBarangay(split[1]);
//            Call<PersonIDResponse> responseCall = service2.generate(personIDRequest);
//            responseCall.enqueue(new Callback<PersonIDResponse>() {
//                @Override
//                public void onResponse(Call<PersonIDResponse> call, Response<PersonIDResponse> response) {
//                    if (response.isSuccessful()) {
//                        SmsManager.getDefault().sendTextMessage(sender, null, "Your One-Time-Pin\n" + response.body().getPerson_id(), sentPI, deliveredPI);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<PersonIDResponse> call, Throwable t) {
//                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });


        }
    }



    @Override
    protected void onResume() {
        SMSReceiver.bindListener(this);
        List<SendStatus> noOfFailedMessages = DB.getInstance(this).sendStatusDao().getFailedMessages();

        btnFailedMessage.setText(String.format("FAILED MESSAGES > %d", noOfFailedMessages.size()));
        initRecyclerView();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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


    @AfterPermissionGranted(011)
    private void askForPermissions() {
        String[] perms = {Manifest.permission.INTERNET,  Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "We need permissions", 123, perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            askForPermissions();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
        }
    }


}