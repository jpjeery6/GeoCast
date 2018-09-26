package jeeryweb.geocast.Reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import jeeryweb.geocast.Utility.SharedPrefHandler;

public class RecieverResetHelpingList extends BroadcastReceiver {

    SharedPrefHandler sharedPrefHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.e("HomeR", "in RecieverResetHelpingList");
        sharedPrefHandler = new SharedPrefHandler(context);
        sharedPrefHandler.resetHelpingUsers();
    }
}
