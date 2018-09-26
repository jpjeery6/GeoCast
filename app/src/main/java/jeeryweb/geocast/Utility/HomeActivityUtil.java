package jeeryweb.geocast.Utility;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import jeeryweb.geocast.Reciever.RecieverResetHelpingList;

/**
 * Created by Debo#Paul on 5/29/2018.
 */

public class HomeActivityUtil {

    final String TAG = "Home";
    SharedPrefHandler sharedPrefHandler;
    Context context;

    public HomeActivityUtil(Context _context) {
        context = _context;
        sharedPrefHandler = new SharedPrefHandler(context);
    }

    public void resetHelpingUsersInfo() {
        Log.e(TAG, "delete helping users list");
        sharedPrefHandler.resetHelpingUsers();
        resetHelpingUsersInfoinTwoHours();
    }

    public void resetHelpingUsersInfoinTwoHours() {

        Intent intent = new Intent(context, RecieverResetHelpingList.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (30 * 60 * 1000), pendingIntent);
    }

    public static String fixDateFormat(String _date) {
        String temp[] = _date.split(" ");
        String ampm = null;

        if (temp[2].equalsIgnoreCase("am"))
            ampm = "AM";
        else if (temp[2].equalsIgnoreCase("a.m"))
            ampm = "AM";
        else if (temp[2].equalsIgnoreCase("a.m."))
            ampm = "AM";
        else if (temp[2].equalsIgnoreCase("pm"))
            ampm = "PM";
        else if (temp[2].equalsIgnoreCase("p.m"))
            ampm = "PM";
        else if (temp[2].equalsIgnoreCase("p.m."))
            ampm = "PM";

        Log.e("fixed datetime", temp[0] + " " + temp[1] + " " + ampm);
        return temp[0] + " " + temp[1] + " " + ampm;
    }
}
