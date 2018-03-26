package jeeryweb.geocast.FirebaseServices;

/**
 * Created by Jeery on 22-02-2018.
 */

import android.content.Intent;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIdListenerService extends FirebaseInstanceIdService {

    //refreshed every 6 months may be
    @Override
    public void onTokenRefresh() {
        //Fetch updated Instance ID token and notify of changes
        Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
        startService(intent);
    }
}
