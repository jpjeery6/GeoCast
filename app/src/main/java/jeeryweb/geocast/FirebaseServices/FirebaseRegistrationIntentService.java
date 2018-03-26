package jeeryweb.geocast.FirebaseServices;

/**
 * Created by Jeery on 22-02-2018.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import android.app.IntentService;
import android.content.Intent;

import jeeryweb.geocast.Utility.SharedPrefHandler;

public class FirebaseRegistrationIntentService  extends  IntentService{


//Attributes**************************************************************
    //Objcets
     private SharedPrefHandler sharedPrefHandler;

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
            String token = instanceID.getToken();
            Log.e(TAG, "FCM Registration Token: " + token);

            sendRegistrationToServer(token);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



//Method 3
    private void sendRegistrationToServer(String token) {
        Log.e(TAG+"tok",token);

        //save the token in shared preferences
        sharedPrefHandler=new SharedPrefHandler(this);
        sharedPrefHandler.saveFcmToken(token);
    }
}