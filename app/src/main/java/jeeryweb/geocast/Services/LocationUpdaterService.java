package jeeryweb.geocast.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.security.Provider;
import java.util.HashMap;

import jeeryweb.geocast.Activities.Home;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

/**
 * Created by Jeery on 22-02-2018.
 */

public class LocationUpdaterService extends Service {
    private final String TAG=getClass().getSimpleName();
    private final String updateLoc = "https://jeeryweb.000webhostapp.com/ProjectLoc/updateLoc.php";

    private LocationManager mLocationManager = null;
    Network network;
    SharedPrefHandler sharedPrefHandler;
    private Intent resultIntent;
    private PendingIntent mPendingIntent;
    Context c;

    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;

    private String user,pass,result,msg="dummy",latt,longi;


    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e("LocationUpdaterService", "onLocationChanged: " + location);
            mLastLocation.set(location);
            latt=Double.toString(location.getLatitude());
            longi=Double.toString(location.getLongitude());
            //I am doing the updation here
            new Thread(new Runnable() {
                public void run() {
                    // a potentially  time consuming task
                    network =new Network(updateLoc,user,pass,msg,latt,longi,"jdnksj",null,null,null,null);
                    result=network.DoWork();
                    if(result!=null)
                    {
                        //create a notification
                        /*
                        resultIntent= new Intent(c, Main3Activity.class);
                        mPendingIntent = PendingIntent.getService(c, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(c)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("Location Service")
                                        .setContentText(result);

                        // Because clicking the notification opens a new ("special") activity, there's
                        // no need to create an artificial back stack.

                        mBuilder.setContentIntent(mPendingIntent);
                        // Sets an ID for the notification
                        int mNotificationId = 783;
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                        */
                    }
                }
            }).start();

        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    @Override
    public void onCreate()
    {
        Log.e("LocationUpdaterService", "onCreate");
        c=this;
        requestLocationUpdates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e("LocationUpdaterService", "onStartCommand");
        //Retrieving username and password
        sharedPrefHandler=new SharedPrefHandler(this);
        HashMap<String, String> userh = sharedPrefHandler.getUserDetails();
        user = userh.get("name");
        pass = userh.get("pass");
        Log.e(TAG + " retrieved session:", user + "  " + pass);
        /*
        user=intent.getStringExtra("usern");
        pass=intent.getStringExtra("passw");
        msg=intent.getStringExtra("mssg");
        latt=intent.getStringExtra("locoLat");
        longi=intent.getStringExtra("locoLon");
        */
        Toast.makeText(this, "Location service starting", Toast.LENGTH_SHORT).show();
        //requestLocationUpdates();
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    //for service
    public IBinder onBind(Intent arg0){
        return null;
    }

    //request location updates regularly
    void requestLocationUpdates()
    {
        //initialize location manager
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }


}
