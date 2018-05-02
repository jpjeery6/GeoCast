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
    //Pushy token
    public static final String PUSHY_TOKEN = "PushyToken";
    //Imei
    public static final String IMEI = "IMEI";

    //Bio * change it .. dont save it in shared pref
    public static final String OCCUPATION = "Occupation";
    public static final String AGE  = "Age";
    public static final String GENDER = "Gender";
    public static final String PHONENO = "PhoneNo";
    public static final String PPPATH = "PPpath";


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

    public void savePushyToken(String pushyToken)
    {
        editor.putString(PUSHY_TOKEN, pushyToken);
        editor.commit();
    }

    public String getFcmToken()
    {
        return pref.getString(FCM_TOKEN, null);
    }

    public String getPushyToken()
    {
        return pref.getString(PUSHY_TOKEN, null);
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

    public void saveIMEI(String imei){
        editor.putString(IMEI ,imei);
        editor.commit();
    }

    public void savePPpath(String path){
        editor.putString(PPPATH ,path);
        editor.commit();
    }

    public String  getPPpath(){
        return pref.getString(PPPATH, null);
    }

    public void saveBio(String occupation,String gender,String age,String phno){
        editor.putString(OCCUPATION ,occupation);
        editor.putString(GENDER ,gender);
        editor.putString(AGE ,age);
        editor.putString(PHONENO ,phno);
        editor.commit();
    }

    public String getUsername()
    {
        return pref.getString(KEY_NAME, null);
    }

    public String getIMEI()
    {
        return pref.getString(IMEI, null);
    }
    public String getOccupation()
    {
        return pref.getString(OCCUPATION, null);
    }
    public String getAge()
    {
        return pref.getString(AGE, null);
    }
    public String getGender()
    {
        return pref.getString(GENDER, null);
    }
    public String getPhoneNo()
    {
        return pref.getString(PHONENO, null);
    }


}
