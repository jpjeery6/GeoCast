package jeeryweb.geocast.FirebaseServices;

/**
 * Created by Jeery on 22-02-2018.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class FirebaseRegistrationIntentService  extends  IntentService{

//    private final String updateFcm = "https://jeeryweb.000webhostapp.com/ProjectLoc/updateFcm.php";


//Attributes**************************************************************
    //Objcets
    APIEndPoint apiEndPoint;
     private SharedPrefHandler sharedPrefHandler;
     private String token;

    // abbreviated tag name
    private final static String TAG = "FireRegIntentService";


//Methods******************************************************************
//Method 1-Constructor
    public FirebaseRegistrationIntentService() {
        super(TAG);
    }


//Method 2
    @Override
    protected void onHandleIntent(Intent intent) {

        Log.e(TAG, "FCM Registration Intent Recieved");

        // Make a call to Instance API
        FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();

        try {
            // request token that will be used by the server to send push notifications
            token = instanceID.getToken();
            Log.e(TAG, "FCM Registration Token: " + token);
            sendRegistrationToServer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//Method 3
    private void sendRegistrationToServer() {
        if (Home.username != null && Home.password != null & token != null) {
            new Thread(new Runnable() {
                public void run() {                                                 //THREAD 1.................
                    // a potentially  time consuming task
                    Network network = new Network(APIEndPoint.updateFcm, Home.username, Home.password, "dummy", "00.00", "00.00", token, null, null, null, null);
                    String result = network.DoWork();
                    if (result != null) {
                        Log.e(TAG, " Fcm " + result);
                    }
                }
            }).start();
        }
    }
}