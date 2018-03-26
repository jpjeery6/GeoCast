package jeeryweb.geocast.Utility;

/**
 * Created by Jeery on 21-02-2018.
 */


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPrefHandler {

//Attributes
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "GeoCast";

    // All Shared Preferences Keys
    public static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    // Password (make variable public to access from outside)
    public static final String KEY_PASS = "pass";
    //Fcm token
    public static final String FCM_TOKEN = "FCMToken";


//Methods
    // Constructor
    public SharedPrefHandler(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public void createLoginSession(String name, String pass){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_PASS, pass);

        // commit changes
        editor.commit();
    }

    public void saveFcmToken(String fcmToken)
    {
        editor.putString(FCM_TOKEN, fcmToken);
        editor.commit();
    }

    public String getFcmToken()
    {
        return pref.getString(FCM_TOKEN, null);
    }

    // Get Login State
    public boolean isLoggedIn(){

        //Boolean defaultValue = _context.getResources().getBoolean(IS_LOGIN);
        //long highScore = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);
        Boolean loggedIn= pref.getBoolean(IS_LOGIN,false);
        return loggedIn;
    }


    // Get stored session data
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));
        // return user
        return user;
    }


    public void logoutUser(Context con){
        editor.putBoolean(IS_LOGIN, false);
        // Dont clear the data from shared preferences instead load the data when next time opened the app
        //editor.clear();
        editor.commit();

    }
}
