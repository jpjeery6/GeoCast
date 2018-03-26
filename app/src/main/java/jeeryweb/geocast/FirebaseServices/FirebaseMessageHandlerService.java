package jeeryweb.geocast.FirebaseServices;

/**
 * Created by Jeery on 22-02-2018.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.FileHelper;


public class FirebaseMessageHandlerService extends FirebaseMessagingService {

//Attributes*********************************************************
    //Objects
    FileHelper fileHelper;


    //Variables
    public static final int MESSAGE_NOTIFICATION_ID = 423105;


//Methods**************************************************************
//Method 1
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String from = remoteMessage.getFrom();
        Log.e("OnMessageRecieved","message");

        //data payload
        if(remoteMessage.getData().size()>0) {
            Log.e("OnMessageRecieved","message data = " + remoteMessage.getData());

            //write all the stuff to the file
            fileHelper = new FileHelper();

            String sender = remoteMessage.getData().get("sender");
            String body = remoteMessage.getData().get("body");
            String timestmp =remoteMessage.getData().get("timestamp");
            String senderLatt =remoteMessage.getData().get("latti");
            String senderLong =remoteMessage.getData().get("longi");
            String clickAction = remoteMessage.getData().get("click_action");

            Log.e("OnMessageRecieved", "message from " + sender);
            Log.e("OnMessageRecieved", "message body = " + body);
            Log.e("OnMessageRecieved", "message time = " + timestmp);
            Log.e("OnMessageRecieved", "message latt = " + senderLatt);
            Log.e("OnMessageRecieved", "message longi = " + senderLong);
            Log.e("OnMessageRecieved ", "message click = " + clickAction);

            fileHelper.writeFile(this,sender,body, timestmp, senderLatt,senderLong);


            //create notification
            //onclick open the message activity
            Intent intent = new Intent(clickAction);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(sender)
                    .setContentText(body).setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}).setAutoCancel(true).setContentIntent(pendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, notificationBuilder.build());

        }

        //click action= "jeeryweb.locationtest.CONTACTS";  change it in php

    }

}
