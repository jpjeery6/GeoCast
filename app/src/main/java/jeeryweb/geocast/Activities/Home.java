package jeeryweb.geocast.Activities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jeeryweb.geocast.Dialogs.MessageInputDialog;
import jeeryweb.geocast.FirebaseServices.FirebaseRegistrationIntentService;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Services.LocationUpdaterService;
import jeeryweb.geocast.Utility.FileHelper;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

/**
 * This class 1.sends the FCM token to the server if running --for the first time-- in a separate thread
 * 2.uploads the last known location in a separate thread
 * 3.sends the message if send button clicked on a separate thread
 * <p>
 * 4.Running a FirebaseRegisterIntentService for getting the token for the first time
 * 5.starting the LocationUpdater Service
 * 6.Starting the MessageRecieverService
 */

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {

//Attributes***************************************************************

    //TAG for Logging
    private final String updateLoc = "https://jeeryweb.000webhostapp.com/ProjectLoc/updateLoc.php";
    private final String sendMsg = "https://jeeryweb.000webhostapp.com/ProjectLoc/uploadMsg.php";
    private final String updateFcm = "https://jeeryweb.000webhostapp.com/ProjectLoc/updateFcm.php";
    private final String lgot = "https://jeeryweb.000webhostapp.com/ProjectLoc/logout.php";
    private final String pullMsg = "https://jeeryweb.000webhostapp.com/ProjectLoc/pullMsg.php";

    //debojyopti11 forked
    //objects
    SharedPrefHandler sharedPrefHandler;
    Handler handler;
    NavigationView navigationView;
    Context con;
    //globally required for location purpose
    private UiSettings mUiSettings;
    private GoogleMap mMap;
    static Location locationObj;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationUpdaterService locationUpdaterService;
    Network network;
    FileHelper fileHelper;
    FloatingActionMenu sendMessageFab;

    //Variables
    private final String TAG = getClass().getSimpleName();
    //to check if my activity is the current activity
    public boolean isInFront;
    //to check if the app is run for the first time
    private boolean firstTime;
    static String username, password;
    String fcmToken;
    String msg, result;

    View mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        statusCheck();


        /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        */

        com.github.clans.fab.FloatingActionButton emergencyMsgFab, customMsgFab;

        sendMessageFab = (FloatingActionMenu) findViewById(R.id.sendmsg_floating_menu);
        emergencyMsgFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.emergencymsgfab);
        customMsgFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.custommsgfab);



        emergencyMsgFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                //send in built message directly
                msg = "Please help me";

                //do this on a new thread
                new Thread(new Runnable() {
                    public void run() {                                                 //THREAD 4.............
                        // a potentially  time consuming task
                        if (locationObj != null) {
                            network = new Network(sendMsg, username, password, msg, Double.toString(locationObj.getLatitude()), Double.toString(locationObj.getLongitude()), "ksdhfj",null,null,null,null);
                            result = network.DoWork();
                            if (result != null) {
                                Log.e(TAG, "send message main thread" + result);
                                //pass this result to UI thread by writing a message to the UI's handler
                                Message m = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putString("result", result);
                                m.setData(bundle);
                                handler.sendMessage(m);
                            }
                        }
                    }
                }).start();
                sendMessageFab.close(true);

            }
        });
        customMsgFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                MessageInputDialog messageInputDialog = new MessageInputDialog();
                messageInputDialog.passFloatMenu(sendMessageFab);
                messageInputDialog.show(getFragmentManager(), "customMsg");

            }
        });
        sendMessageFab.setClosedOnTouchOutside(true);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home_nav_home);

        //getting navbar header items- image , name ,welcome
        View homeNavHeader = navigationView.getHeaderView(0);
        LinearLayout navHeaderLayout = (LinearLayout) homeNavHeader.findViewById(R.id.home_nav_layout);

        //setting up objects ................................................................................
        sharedPrefHandler = new SharedPrefHandler(this);
        con = this;

        //logged in first time or not --required for fcm token sending........................................
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            firstTime = bundle.getBoolean("firstTime");


        //Retrieving username and password ..................................................................
        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");
        Log.e(TAG + " retrieved session:", username + "  " + password);


        //if first time run firebase service and send token to server if not dont run.................................
        if (firstTime) {
            Log.e(TAG, "first time yes");
            //create chat history saving file
            fileHelper = new FileHelper();
            fileHelper.createFile(this);
            //noone should interrupt while registering so doing it in a service
            if (checkPlayServices()) {
                Intent intent = new Intent(this, FirebaseRegistrationIntentService.class);
                startService(intent);
            }
            fcmToken = sharedPrefHandler.getFcmToken();
            if (username != null && password != null & fcmToken != null) {
                new Thread(new Runnable() {
                    public void run() {                                                 //THREAD 1.................
                        // a potentially  time consuming task
                        network = new Network(updateFcm, username, password, "dummy", "00.00", "00.00", fcmToken,null,null,null,null);
                        result = network.DoWork();
                        if (result != null) {
                            Log.e(TAG, " Fcm " + result);
                        }
                    }
                }).start();
            }
        } else {
            Log.e(TAG, "first time no");
            fcmToken = sharedPrefHandler.getFcmToken();
            Log.e(TAG + " retrieved token:", fcmToken);
        }


        //getting widgets........................................................................................


        //set username in navigation drawer
        TextView navUsername = (TextView)homeNavHeader.findViewById(R.id.home_nav_username);
        navUsername.setText(username.toUpperCase());


        //setting up the map fragment view ...........................................................................
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);


        //getting the last known location.............................................................................
        //might fail if no last known location available
        if (username != null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        locationObj = location;
                        //set the marker in the map only if the map is already ready
                        if (mMap != null) {
                            LatLng myloc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(myloc).title("My Current Location"));

                            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude()); //Store these lat lng values somewhere. These should be constant.
                            CameraUpdate cam = CameraUpdateFactory.newLatLngZoom(
                                    coordinate, 13);
                            mMap.animateCamera(cam);
                            mMap.setMyLocationEnabled(true);
                            Log.e("maps", "onMapReady Callback my location enabled");
                        }

                        // Logic to handle location object
                        Log.e(TAG + " lattitude", Double.toString(location.getLatitude()));
                        Log.e(TAG + " longitude", Double.toString(location.getLongitude()));
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
                        String format = simpleDateFormat.format(new Date());
                        Log.v(TAG + "timestamp", format);


                        msg = "dummy";
                        //update database with this location
                        //do this on a new thread
                        new Thread(new Runnable() {
                            public void run() {                                                     //THREAD 2............
                                // a potentially  time consuming task
                                network = new Network(updateLoc, username, password, msg, Double.toString(locationObj.getLatitude()), Double.toString(locationObj.getLongitude()), "kjdfjk",null,null,null,null);
                                result = network.DoWork();
                                if (result != null) {
                                    Log.e(TAG, "toast main thread" + result);
                                    if (result.contains("some keyword")) { //we are not sending this thats why some keyword
                                        //pass this result to UI thread by writing a message to the UI's handler
                                        Message m = Message.obtain();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("result", result);
                                        m.setData(bundle);
                                        handler.sendMessage(m);
                                    }
                                }
                            }
                        }).start();

                        //start the services------------only if location is known
                        //location updater service
                        if (!isMyServiceRunning(LocationUpdaterService.class)) {
                            locationUpdaterService = new LocationUpdaterService();
                            Intent intent1 = new Intent(con, LocationUpdaterService.class);
                            intent1.putExtra("usern", username);
                            intent1.putExtra("passw", password);
                            intent1.putExtra("mssg", msg);
                            intent1.putExtra("locoLat", Double.toString(locationObj.getLatitude()));
                            intent1.putExtra("locoLon", Double.toString(locationObj.getLongitude()));
                            startService(intent1);
                        }


                    }
                }
            });

        }


        //setting listeners on buttons.............................................................................

        //setting listner on nav header layout to go to MyProfile Activity
        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to MyProfile Activity
                Intent i = new Intent(con, MyProfile.class);
                con.startActivity(i);
            }
        });

        //message send button
        /*
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = message.getText().toString();

                //do this on a new thread
                new Thread(new Runnable() {
                    public void run() {                                                 //THREAD 4.............
                        // a potentially  time consuming task
                        if (locationObj != null) {
                            network = new Network(sendMsg, username, password, msg, Double.toString(locationObj.getLatitude()), Double.toString(locationObj.getLongitude()), "ksdhfj");
                            result = network.DoWork();
                            if (result != null) {
                                Log.e(TAG, "send message main thread" + result);
                                //pass this result to UI thread by writing a message to the UI's handler
                                Message m = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putString("result", result);
                                m.setData(bundle);
                                handler.sendMessage(m);
                            }
                        }
                    }
                }).start();

            }
        });
        */
        //done-------------------------------------------------------------------------------------

        Log.e("Main2Activity Threads=", " " + Thread.activeCount());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                result = msg.getData().get("result").toString();
                Toast.makeText(con, "Message Sent Successfully", Toast.LENGTH_LONG).show();
            }
        };
    }

    //when this activity restarts or resumes check if the location service is running or not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.setMinZoomPreference(10.0f);
        //mMap.setMaxZoomPreference(20.0f);


        //changing the position of my location button
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(16, 200, 16, 16);

        mMap.setMyLocationEnabled(true);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        //mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setAllGesturesEnabled(true);
        Log.e("maps", "onMapReady Callback");


    }




    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 1)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.home_nav_home);
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(sendMessageFab.isOpened())
        {
            sendMessageFab.close(true);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_action_logout) {
            //logout...................................................................................................
            Log.e("nav_logout= ","true");
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(con, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(con);
            }
            builder.setTitle("Log out")
                    .setMessage("Are you sure you want to log out? This will delete all your messages from the device")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            sharedPrefHandler.logoutUser(con);
                            fileHelper =new FileHelper();
                            fileHelper.deleteFile(con);

                            //set loggedIn bit in database to Zero
                            //do this on a new thread
                            new Thread(new Runnable() {
                                public void run() {                                                     //THREAD 3............
                                    // a potentially  time consuming task
                                    network = new Network(lgot, username, password, "dummy", "00.00","00.00", "dummy",null,null,null,null);
                                    result = network.DoWork();
                                    if (result != null) {
                                        Log.e(TAG, "toast main thread" + result);
                                    }
                                }
                            }).start();
                            Toast.makeText(con, "Logged out Successfully", Toast.LENGTH_LONG).show();

                            // After logout redirect user to going Activity
                            Intent i = new Intent(con, Login.class);
                            con.startActivity(i);
                            finish();

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            //logout done

            return true;
        }
        else if(id == R.id.home_action_downloadchat)
        {
            //download messages ........................................................................................
            fileHelper = new FileHelper();
            if (fileHelper.emptyFile(con)) {
                new Thread(new Runnable() {
                    public void run() {                                                     //THREAD 3............
                        // a potentially  time consuming task
                        network = new Network(pullMsg, username, password, "dummy", "00.00", "00.00", "dummy", null,null,null,null);
                        result = network.DoWork();
                        if (result != null) {
                            Log.e(TAG, "toast main thread=" + result);
                            //write the results in the file
                            //.................
                            fileHelper.writeFile(con, result);

                        }

                    }
                }).start();
                Toast.makeText(con, "All Messages downloaded", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(con, "You already have messages", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.home_action_settings) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home_nav_home) {
            // Handle the camera action
        } else if (id == R.id.home_nav_inbox) {
            // Handle the inbox........................................................................................
            //go to messages/contacts activity
            Intent i = new Intent(con, Inbox.class);
            i.putExtra("sender","dummy");
            i.putExtra("msg","dummy");
            con.startActivity(i);

        } else if (id == R.id.home_nav_sent) {

        } else if (id == R.id.home_nav_tools) {

        }

        else if (id == R.id.home_nav_share) {

        } else if (id == R.id.home_nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(Home.this, "Gps not enabled",Toast.LENGTH_SHORT ).show();

            if(!manager.isProviderEnabled( LocationManager.NETWORK_PROVIDER)){
                Toast.makeText(Home.this, "Network provider not enabled",Toast.LENGTH_SHORT ).show();
                buildAlertMessageNoGps();
            }



        }
        getLocationMode(Home.class);
        Toast.makeText(Home.this, "Location service is working fine",Toast.LENGTH_SHORT).show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        Intent i = new Intent(Yourclassname.this,Home.class); startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public int getLocationMode(Class context)
    {
        return  Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
    }
}
