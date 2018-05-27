package jeeryweb.geocast.PushyServices;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Activities.MessageExpanded;
import jeeryweb.geocast.Activities.ReliabilityResponse;

public class PushyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("pushy rec","something is recieved fron puahy");
        String notificationTitle = null;
        String clickParameter = null;
        String msgTime = "2018-05-08 09:00:00 pm";
        String msgLatt = "26.98";
        String msgLon = "93.8";
        String notificationText = "Test body";

        if (intent.getStringExtra("parameter") != null) {
            clickParameter = intent.getStringExtra("parameter");
        }

        if (intent.getStringExtra("sender") != null) {
            notificationTitle = intent.getStringExtra("sender");
        }

        // Attempt to extract the "message" property from the payload: {"message":"Hello World!"}
        if (intent.getStringExtra("message") != null) {
            notificationText = intent.getStringExtra("message");
        }
        if(intent.getStringExtra("time") == null)
            Log.e("Pushy Reciever","time null");

        if(intent.getStringExtra("latti") == null)
            Log.e("Pushy Reciever","latti null");
        if(intent.getStringExtra("longi") == null)
            Log.e("Pushy Reciever","longi null");

        if (intent.getStringExtra("time") != null) {
            Log.e("Pushy Reciever","time null");
            msgTime = intent.getStringExtra("time");
        }
        if (intent.getStringExtra("latti") != null) {
            Log.e("Pushy Reciever","latti null");
            msgLatt = intent.getStringExtra("latti");
        }
        if (intent.getStringExtra("longi") != null) {
            Log.e("Pushy Reciever","longi null");
            msgLon = intent.getStringExtra("longi");
        }

        // Prepare a notification with vibration, sound and lights
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                //.setAutoCancel(true)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intentAct= null;
        if(clickParameter.contains("msg")){
            Log.e("puhy messgae rec","type message");
            intentAct  = new Intent(context,MessageExpanded.class);
            intentAct.putExtra("sender", notificationTitle);
            intentAct.putExtra("msg", notificationText);
            intentAct.putExtra("time",msgTime );
            intentAct.putExtra("latti",msgLatt );
            intentAct.putExtra("longi",msgLon );
        }
        else if(clickParameter.contains("rel"))
        {
            Log.e("puhy messgae rec","type rel req");
            intentAct  = new Intent(context,ReliabilityResponse.class);
            intentAct.putExtra("RReqUsername",notificationTitle);
        }

        else if(clickParameter.contains("reqrep"))
        {
            Log.e("puhy messgae rec","type rel req");
            intentAct  = new Intent(context,ReliabilityResponse.class);
            intentAct.putExtra("RReqUsername",notificationTitle);
        }
        else if(clickParameter.contains("ack")){
            Log.e("puhy messgae rec","type recep from message acknoledgement");
            intentAct  = new Intent(context,Home.class);
            intentAct.putExtra("helper", notificationTitle);
            Log.e("puhy messgae rec", "helper is ready "+ notificationText);
        }



        builder.setContentIntent(PendingIntent.getActivity(context, 0, intentAct, PendingIntent.FLAG_UPDATE_CURRENT));
        // You must set the Notification Channel ID
        // if your app is targeting API Level 26 and up (Android O)
        // More info: http://bit.ly/2Bzgwl7
        builder.setChannelId("myNotificationChannelId");
        builder.setAutoCancel(true);

        // Get an instance of the NotificationManager service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Build the notification and display it
        notificationManager.notify(1, builder.build());
    }
}