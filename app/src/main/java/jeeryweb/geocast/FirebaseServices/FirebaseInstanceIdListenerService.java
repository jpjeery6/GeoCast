package jeeryweb.geocast.FirebaseServices;

/**
 * Created by Jeery on 22-02-2018.
 */

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import jeeryweb.geocast.Utility.SharedPrefHandler;

public class FirebaseInstanceIdListenerService extends FirebaseInstanceIdService {

    private SharedPrefHandler sharedPrefHandler;

    //refreshed every 6 months may be
    @Override
    public void onTokenRefresh() {
        Log.e("Firebase inst 6month","new token refreshed");
        saveInSP(FirebaseInstanceId.getInstance().getToken());
        //Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
        startService(intent);
    }

    void saveInSP(String token)
    {
        sharedPrefHandler =new SharedPrefHandler(getApplicationContext());
        sharedPrefHandler.saveFcmToken(token);
    }
}
