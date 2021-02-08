package com.example.gatewayapp.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.example.gatewayapp.Database.DB;
import com.example.gatewayapp.Database.Models.SendStatus;
import com.example.gatewayapp.Helpers.ASCIIToChar;
import com.example.gatewayapp.Contracts.ISendData;
import com.example.gatewayapp.RetrofitService;
import com.example.gatewayapp.ContractModels.SendDataRequest;
import com.example.gatewayapp.ContractModels.SendDataResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SMSReceiver extends BroadcastReceiver  {
    private static final String TAG = "GATEWAY_APP";
    public static final String pdu_type = "pdus";

    private final int USER_ID_INDEX = 0;
    private final int CHECKER_ID_INDEX  = 1;
    private final int LOCATION_INDEX = 2;
    private final int TEMPERATURE_INDEX = 3;
    private final int PURPOSE_INDEX = 4;
    private final int TIME_INDEX = 5;



    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (Object o : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getMessageBody();
            mListener.messageReceived(sender, message);
        }
    }

    public static void bindListener(MessageListener listener){
        mListener = listener;
    }
    /**
     * Called when the BroadcastReceiver is receiving an Intent broadcast.
     *
     * @param context  The Context in which the receiver is running.
     * @param intent   The Intent received.
     */
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if(intent.getA)
//        // Get the SMS message.
//        Bundle bundle = intent.getExtras();
//        SmsMessage[] msgs;
//        String strMessage = "";
//        String format = bundle.getString("format");
//        // Retrieve the SMS message received.
//        Object[] pdus = (Object[]) bundle.get(pdu_type);
//        if (pdus != null) {
//            // Check the Android version.
//            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
//            // Fill the msgs array.
//            msgs = new SmsMessage[pdus.length];
//            for (int i = 0; i < msgs.length; i++) {
//                // Check Android version and use appropriate createFromPdu.
//                if (isVersionM) {
//                    // If Android version M or newer:
//                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                } else {
//                    // If Android version L or older:
//                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                }
//                // Build the message to show.
////                strMessage += "SMS from " + msgs[i].getOriginatingAddress();
//                strMessage = msgs[i].getMessageBody();
//                // Log and display the SMS message.
////                Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
//                Toast.makeText(context, "Processing message", Toast.LENGTH_SHORT).show();
//                sendToAPI(context, strMessage);
//            }
//        }
//    }

    private void sendToAPI(Context context, String message)
    {
        // Push to SendStatusList
        Retrofit retrofit = RetrofitService.RetrofitInstance(context);
        ISendData service = retrofit.create(ISendData.class);

        List<String> information =  ASCIIToChar.convert(message);

        SendDataRequest requestPerson = new SendDataRequest();
        requestPerson.setUser_id(information.get(USER_ID_INDEX));
        requestPerson.setChecker_id(information.get(CHECKER_ID_INDEX));
        requestPerson.setLocation(information.get(LOCATION_INDEX));
        requestPerson.setTemperature(information.get(TEMPERATURE_INDEX));
        requestPerson.setPurpose(information.get(PURPOSE_INDEX));
        requestPerson.setTime(information.get(TIME_INDEX));



        Call<SendDataResponse> responsePersonCall = service.sendPersonLog(requestPerson);
        responsePersonCall.enqueue(new Callback<SendDataResponse>() {
            @Override
            public void onResponse(Call<SendDataResponse> call, Response<SendDataResponse> response) {
                if (response.body().getCode().equals("200")) {
                    SendStatus sendStatus = new SendStatus();
                    sendStatus.setId(DB.getInstance(context).sendStatusDao().getLastId() + 1);
                    sendStatus.setData_message(message);
                    sendStatus.setStatus("Send");
                    DB.getInstance(context).sendStatusDao().create(sendStatus);
                    //Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendDataResponse> call, Throwable t) {
                SendStatus sendStatus = new SendStatus();
                sendStatus.setId(DB.getInstance(context).sendStatusDao().getLastId() + 1);
                sendStatus.setData_message(message);
                sendStatus.setStatus("Failed");
                DB.getInstance(context).sendStatusDao().create(sendStatus);
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
