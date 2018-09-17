package jeeryweb.geocast.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Dialogs.MessageInputDialog;
import jeeryweb.geocast.PushyServices.PushyToken;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Services.LocationUpdaterService;
import jeeryweb.geocast.Utility.FileHelper;
import jeeryweb.geocast.Utility.HomeActivityUtil;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;
import me.pushy.sdk.Pushy;

/**
 * This class 1.sends the FCM token to the server if running --for the first time-- in a separate thread
 * 2.uploads the last known location in a separate thread
 * 3.sends the message if send button clicked on a separate thread
 * <p>
 * 4.Running a FirebaseRegisterIntentService for getting the token for the first time
 * 5.starting the LocationUpdater Service
 * 6.Starting the MessageRecieverService
 */

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

//Attributes**************************************************************************************************************
    public static String username, password;
    public static  Location locationObj = null;
    public int radius;
    int counter = 0;

    //Variables
    private final String TAG = getClass().getSimpleName();
    public boolean internet = true;
    //to check if my activity is the current activity
    public boolean isInFront;
    //to check if the app is run for the first time
    private boolean firstTime, byNotification;
    private boolean firstZoom = false;
    private int greenZoom = 0;
    private jeeryweb.geocast.Activities.Settings geoCastSettings;

    //user defined class objects
    APIEndPoint apiEndPoint;
    SharedPrefHandler sharedPrefHandler;
    Network network;
    PushyToken pushyTokenObj;
    FileHelper fileHelper;
    HomeActivityUtil homeActivityUtil;    //contains live iser methhods
    //for volley
    private RequestQueue requestQueue;

    NavigationView navigationView;
    public static Context con;
    Activity activity;
    TextView nearbyInfo;

    Marker mCurrLocationMarker;
    Circle mcurrentCircle;
    static List<Marker> nearbyMarkers;
    List<LatLng> nearbyLatlang;
    List<String> nearbyUsername;
    String fcmToken,pushyToken;
    String msg, result;

    String helper = null, lattihelper = null, longihelper = null;
    // for marker animation

    HashMap<String, Boolean> markerAnimationhelper;
    //simpledate ofrmat
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");


    //widgetss
    View mapView;
    FloatingActionMenu sendMessageFab;
    com.github.clans.fab.FloatingActionButton emergencyMsgFab, customMsgFab;
    public static ProgressDialog dialog;

    //Objects
    private BroadcastReceiver receiver;
    // required for location purpose
    private int mode;
    private UiSettings mUiSettings;
    private GoogleMap mMap;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;


//Methods**************************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pushy.listen(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        con = this;
        activity = this;
        geoCastSettings = new jeeryweb.geocast.Activities.Settings();
        radius = Integer.parseInt(geoCastSettings.getRadiusSetting(con));
        Log.e("Home ", "Rd val = " + radius);
        if (radius == 0) {
            //Log.e("Rd value","entered here");
            radius = 30;

        }

        Log.e("Home ", "Rd val = " + radius);
        nearbyLatlang = new ArrayList<>();
        nearbyMarkers = new ArrayList<>();
        nearbyUsername = new ArrayList<>();
        markerAnimationhelper = new HashMap<>();

        //setting up broadcast reciever for internet connection checking
        receiver = new BroadcastReceiver() {
            final Snackbar snackbar = Snackbar.make(findViewById(R.id.home_content_parent), "No Internet Connection", Snackbar.LENGTH_INDEFINITE);

            @Override
            public void onReceive(Context context, Intent arg1) {
                //do something based on the intent's action
                boolean isConnected = arg1.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (isConnected) {
                    Log.e("broadcast reciever", "no network");
                    snackbar.show();
                    internet = false;
                    fadeView();
                } else {
                    Log.e("broadcast reciever", "network aagaya");
                    if (snackbar != null) {
                        internet = true;
                        snackbar.dismiss();
                        restoreView();
                        //Log.e("broadcast reciever", "snackbar  mila");
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));


        //getting widgets
        sendMessageFab = findViewById(R.id.sendmsg_floating_menu);
        emergencyMsgFab = findViewById(R.id.emergencymsgfab);
        customMsgFab = findViewById(R.id.custommsgfab);
        nearbyInfo = findViewById(R.id.nearbyUersInfo);


        //navigation bar
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home_nav_home);

        //getting navbar header items- image , name ,welcome
        View homeNavHeader = navigationView.getHeaderView(0);
        LinearLayout navHeaderLayout = homeNavHeader.findViewById(R.id.home_nav_layout);


        //setting up objects ................................................................................
        sharedPrefHandler = new SharedPrefHandler(this);
        homeActivityUtil = new HomeActivityUtil(this);

        //debugging

//        sharedPrefHandler.saveHelpingUser("dev jeery");
//        sharedPrefHandler.saveHelpingUser("Random");
//        sharedPrefHandler.saveHelpingUser("kalpa");
        //end debugging

        //logged in first time or not --required for pushy token sending........................................
        //we will get this intent from 1. login/register activity 2. Pushy reciever for shwing green marker in map
        Bundle bundle = getIntent().getExtras();
        Intent i = getIntent();
        if (bundle != null){
            //1
            firstTime = bundle.getBoolean("firstTime");
            //2
            if(i.hasExtra("helper")){
                helper = bundle.getString("helper");
               Log.e(TAG, "Helper::: "+helper);
               Toast.makeText(con, "Helper:::::: "+helper, Toast.LENGTH_SHORT).show();
            }
            if (i.hasExtra("lattihelper")) {
                lattihelper = bundle.getString("lattihelper");
                Log.e(TAG, "Helper::: " + lattihelper);
                Toast.makeText(con, "Helper:::::: " + lattihelper, Toast.LENGTH_SHORT).show();
            }
            if (i.hasExtra("longihelper")) {
                longihelper = bundle.getString("longihelper");
                Log.e(TAG, "Helper::: " + longihelper);
                Toast.makeText(con, "Helper:::::: " + longihelper, Toast.LENGTH_SHORT).show();
            }

            if (helper != null && lattihelper != null && longihelper != null) {
                byNotification = true;
            }
        }


        //Retrieving username and password ..................................................................
        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");
        Log.e(TAG + " retrieved session:", username + "  " + password);


        //if first time run pushy service and send token to server if not dont run.................................
        if (firstTime) {
            Log.e(TAG, "first time yes");
            //create chat history saving file
            fileHelper = new FileHelper();
            fileHelper.createFile(this);
            //noone should interrupt while registering so doing it in a service
            if (checkPlayServices()) {
                //here we should have taken the firebase token but it is generated earlier only
                Log.e("googlePlayServices", "yes");
            }
            pushyTokenObj = new PushyToken();
            pushyTokenObj.getPushyDeviceToken(this);
            if(pushyToken == null)
                Log.e("Home ","pushyToken null");
            else
                sharedPrefHandler.savePushyToken(pushyToken);

        } else {
            Log.e(TAG, "first time no");
            pushyToken = sharedPrefHandler.getPushyToken();
            //even if it is not the first time upload the fcm token in case it has changed
            Log.e(TAG + " retrieved token:", pushyToken);
        }


        //getting widgets........................................................................................
        //set username in navigation drawer
        TextView navUsername = homeNavHeader.findViewById(R.id.home_nav_username);
        navUsername.setText(username.toUpperCase());


        //setting up the map fragment view ...........................................................................
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapView = mapFragment.getView();

        //volley request queue
        requestQueue = Volley.newRequestQueue(this);

        //setting listeners on buttons.............................................................................
        emergencyMsgFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (internet) {
                    //TODO something when floating action menu first item clicked
                    //send in built message directly
                    msg = "Please help me please";

                    dialog = new ProgressDialog(Home.this);
                    dialog.setMessage("Sending Emergency Message");
                    dialog.show();

                    //send message using volley
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.sendMsg,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.dismiss();
                                    Toast.makeText(con, "Message Sent Successfully", Toast.LENGTH_LONG).show();


                                    homeActivityUtil.resetHelpingUsersInfo();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }){
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("Username", username);
                            params.put("Password", password);
                            params.put("Latitude", Double.toString(locationObj.getLatitude()));
                            params.put("Longitude", Double.toString(locationObj.getLongitude()));
                            params.put("Message", msg);
                            params.put("Timestamp", HomeActivityUtil.fixDateFormat(dateFormat.format(new Date())));
                            params.put("Radius", geoCastSettings.getRadiusSetting(con));
                            params.put("Reliable", geoCastSettings.getReliableSetting(con));


                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);

                    sendMessageFab.close(true);
                }
            }
        });

        customMsgFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (internet) {
                    //TODO something when floating action menu second item clicked
                    final MessageInputDialog messageInputDialog = new MessageInputDialog();
                    messageInputDialog.passFloatMenu(sendMessageFab);
                    messageInputDialog.show(getFragmentManager(), "customMsg");

                    sendMessageFab.close(true);

                }
            }
        });

        sendMessageFab.setClosedOnTouchOutside(true);


        //setting listner on nav header layout to go to MyProfile Activity
        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to MyProfile Activity
                Intent i = new Intent(con, MyProfile.class);
                con.startActivity(i);
            }
        });

        Log.e("Main2Activity Threads=", " " + Thread.activeCount());


        //execution going to the callback
        mapFragment.getMapAsync(this);


        mapView.post(new Runnable() {
            @Override
            public void run() {

                //if(firstZoom == false)
                updateLocFromHome();
                getRealTimeLocations();

                //homeLocationSuccessDoWork();
                mapView.postDelayed(this,10000);
                Log.e("Home", "one loop done");
            }
        });

        //done-------------------------------------------------------------------------------------



    }

    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.e("maps", "onMapReady async callback");


        //set a map style
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        //my location button---------
        //changing the position of my location button
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right top
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(16, 200, 16, 16);


        //enable my location function
        mMap.setMyLocationEnabled(true);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setCompassEnabled(true);
        //mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setAllGesturesEnabled(true);
        Log.e("maps", "map is ready with my location button and everything, get location now");

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.e("info window clicked", marker.getTitle());
                //go to MyProfile Activity
                Intent i = new Intent(con, ReliabilityRequest.class);
                i.putExtra("Username", marker.getTitle());
                con.startActivity(i);
            }
        });

        if (byNotification) {
            LatLng helperLoc = new LatLng(Double.parseDouble(lattihelper), Double.parseDouble(longihelper));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(helperLoc));

        } else {
            // Zoom out just a little
            //   mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 0.5f));
        }
        getLocSettings();

    }


    private void getLocSettings() {
        //try to get current location
        //check settings first
        try {
            mode = android.provider.Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE);
            Log.e("settings mode::::=", String.valueOf(mode));
        } catch (Exception e) {
            Log.e("settings mode=", "exception setting not found");
            e.printStackTrace();
        }
        if (mode == 0)
            showSettingsAlert(0); //ask to enable gps
        else if (mode != 0 && mode != 3)
            showSettingsAlert(1); //ask to set to high accuracy mode
        else if (mode == 3) {
            Log.e("get Loc", "mode is 3 settings ok get loc now");
            //all settings ok try to get location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            tryToRequestLocationOurself();

        }
    }


    @SuppressLint("MissingPermission")
    private void tryToRequestLocationOurself() {
        Log.e("tryToReqOurself", "entered func");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (!isMyServiceRunning(LocationUpdaterService.class)) {
            Intent intent1 = new Intent(con, LocationUpdaterService.class);
            startService(intent1);
        }

        Log.e("tryToReqOurself", "requesting loca updtaes");

        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }


    //it is like an attribute
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                //Log.e("location callback", "on result func called but locationResult is null");
                //getLoc();
            } else {
                //Log.e("location callback", "on result func called ");
                //homeLocationSuccessDoWork();
                locationObj = locationResult.getLastLocation();
                for (Location location : locationResult.getLocations()) {
                    //Log.e("location callback itr", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    locationObj = location;
                    //update home UI repeatedly in the for loop.
                    //homeLocationSuccessDoWork();
                }
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            if (locationAvailability.isLocationAvailable()) {
                //returns true the onLocationResult may not always be called regularly, however the device location is known
                //on result callback can happen
                //Log.e("location callback", "location is available");
                tryToGetLastLocation();

            } else {
                //on location result will not be called
                //Log.e("location callback", "location not available");
                tryToGetLastLocation();
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void tryToGetLastLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location == null) {
                    Log.e("tryTogetlastloc", "location is null");
                } else {
                    Log.e("tryTogetlastloc", "got last location success");
                    locationObj = location;
                    //homeLocationSuccessDoWork();
                }
            }
        });
    }

    private void updateLocFromHome() {
        if (locationObj != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.updateLoc,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("Response home locupdt", response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Can't cannot to Server", Toast.LENGTH_SHORT).show();
                            Log.e("Volley Real time loc", " kk" + error.getMessage());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Username", username);
                    params.put("Password", password);
                    params.put("Latitude", Double.toString(locationObj.getLatitude()));
                    params.put("Longitude", Double.toString(locationObj.getLongitude()));
                    //params.put("Radius", String.valueOf(radius));

                    return params;
                }
            };

            requestQueue.add(stringRequest);

        }
    }


    private void getRealTimeLocations() {
        //do this thing using volley bcz handler may the one creating the lag

        Log.e("Home", "n/w call");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.nearbyusers,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response real time =", response);

                        String[] nearby = response.split("nearby");
                        //Log.e("no of nearby users", String.valueOf(nearby.length));

                        if (nearbyLatlang != null)
                            nearbyLatlang.clear();
                        if (nearbyUsername != null)
                            nearbyUsername.clear();

                        for (int i = 1; i < nearby.length; i++)   //no of users nearby
                        {
                            //Log.e("nearby[]", String.valueOf(i) + " :" + nearby[i]);
                            String[] nearbylattlong = nearby[i].split("\\|");
                            //Log.e("location of 1 user", String.valueOf(nearbylattlong.length));
                            //Log.e("nearbylattlong",nearbylattlong[0]);
                            String nearUser = nearbylattlong[0];
                            String nearbylatt = nearbylattlong[1];
                            String nearbylong = nearbylattlong[2];         //each user's latt long
                            //create new marker
                            LatLng latLng = new LatLng(Double.valueOf(nearbylatt), Double.valueOf(nearbylong));
                            nearbyLatlang.add(i - 1, latLng);
                            nearbyUsername.add(i - 1, nearUser);
                        }
                        Log.e("Home Get Real Time Loc", "n/w call response");


                        homeLocationSuccessDoWork();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Can't cannot to Server", Toast.LENGTH_SHORT).show();
                        Log.e("Volley Real time loc", " kk" + error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Username", username);
                params.put("Password", password);
                params.put("Latitude", Double.toString(locationObj.getLatitude()));
                params.put("Longitude", Double.toString(locationObj.getLongitude()));
                params.put("Radius", String.valueOf(radius));

                return params;
            }
        };

        requestQueue.add(stringRequest);


    }



    void homeLocationSuccessDoWork() {
        //Got Location
        //Log.e("homeLocationSuccess", "entered func");
        if (mMap == null)
            Log.e("homeLocationSuccess", "map is null");
        if (locationObj == null)
            Log.e("homeLocationSuccess", "locationObj is null");

        //set the marker in the map only if the map is already ready
        if (mMap != null && locationObj != null) {

            Log.e("Home", "Delete everything from map");
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
                Log.e("Home", "curr location removed  ");
            }

            if (mcurrentCircle != null) {
                mcurrentCircle.remove();
                Log.e("Home", "curr circle removed ");
            }

            if (nearbyMarkers != null) {
                removeNearbyUsersMarker();
                Log.e("Home", "remove nearby users ");
            }


            //Place current location marker
            //Log.e("homeLocationSuccess", "set new marker ");
            LatLng latLng = new LatLng(locationObj.getLatitude(), locationObj.getLongitude());
            if (!firstZoom && !byNotification) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                firstZoom = true;
            }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mCurrLocationMarker = mMap.addMarker(markerOptions);
            if (radius == 0)
                radius = 30;

            Log.e("Home radius", " value =" + radius);

            mcurrentCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius * 1000)  //radius in KM , parameter in meters
                    .strokeColor(getResources().getColor(R.color.primaryLightColor))
                    .strokeWidth(4)
                    .fillColor(0x22AAAAAA));

            Log.e("Home", "red dot and circle added");
            //place markers at nearby locations
            if (nearbyLatlang != null) {
                Set<String> helpersList = sharedPrefHandler.getHelpingUser();

                Log.e("Home", "number of markesr deleted  " + nearbyMarkers.size());

                //Log.e("nearbyLatlang=", String.valueOf(nearbyLatlang.size()));
                for (int i = 0; i < nearbyLatlang.size(); i++)   //no of users nearby
                {
                    //Log.e("nearby[]", String.valueOf(i) + " :");
                    markerOptions = new MarkerOptions();
                    markerOptions.position(nearbyLatlang.get(i));
                    markerOptions.snippet("Set as Reliable User?");
                    markerOptions.title(nearbyUsername.get(i));
                    Log.e("HomeP", String.valueOf(helpersList));

                    if (isHelpingUser(nearbyUsername.get(i), helpersList)) {
                        Log.e("HomeP", "helping user");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        if (!markerAnimationhelper.containsKey(nearbyUsername.get(i))) {
                            Log.e("zoom", "in zoon");
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearbyLatlang.get(i), 16));
                            markerAnimationhelper.put(nearbyUsername.get(i), true);
                        }


//                        if(helpersList.size() != greenZoom)
                        //                          mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nearbyLatlang.get(i), 15));
//                        greenZoom++;
                    } else
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    nearbyMarkers.add(i, mMap.addMarker(markerOptions));
                }
                nearbyInfo.setText(nearbyLatlang.size() + " users nearby ");
                //    mCurrLocationMarker = mMap.addMarker(markerOptions);   //to make it on the top
                Log.e("Home", "No of markers added=  " + nearbyMarkers.size());
            }
            //move map camera
//            if(firstZoom ==false) {
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
//                firstZoom = true;
//            }

        }
    }


    private void removeNearbyUsersMarker() {
        if (nearbyMarkers != null) {
            Log.e("No of markers removed= ", " " + nearbyMarkers.size());
            for (int i = 0; i < nearbyMarkers.size(); i++) {
                Log.e("removing nearby markers", "one after another");
                nearbyMarkers.get(i).remove();
            }
            nearbyMarkers.clear();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check for the integer request code originally supplied to startResolutionForResult().
        try {
            mode = android.provider.Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);
            Log.e("settings mode now:::=", String.valueOf(mode));
        } catch (Exception e) {
            Log.e("settings mode=", "exception setting not found");
            e.printStackTrace();
        }

        if (mode != 3)
            finish();
        else {
            Log.e("okokok", "everything setting  ok");
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            tryToRequestLocationOurself();
        }

        Log.e("on activity result rec=", String.valueOf(requestCode));

    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */

    public void showSettingsAlert(int mode) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        if (mode == 0)
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        else if (mode == 1)
            alertDialog.setMessage("GPS is not set to High Accuracy Mode. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 103);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog.setCancelable(false);
        Dialog gpsSettingDialog = alertDialog.create();
        gpsSettingDialog.setCanceledOnTouchOutside(false);
        // Showing Alert Message
        alertDialog.show();
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




    private void fadeView() {
        View view = findViewById(R.id.home_content_parent);

        sendMessageFab.setMenuButtonColorNormal(Color.WHITE);
        sendMessageFab.setMenuButtonColorPressed(Color.WHITE);
        sendMessageFab.setMenuButtonColorRipple(Color.WHITE);

        emergencyMsgFab.setColorNormal(Color.WHITE);
        emergencyMsgFab.setColorPressed(Color.WHITE);
        emergencyMsgFab.setColorRipple(Color.WHITE);

        customMsgFab.setColorNormal(Color.WHITE);
        customMsgFab.setColorPressed(Color.WHITE);
        customMsgFab.setColorRipple(Color.WHITE);
    }

    private void restoreView() {
        View v = findViewById(R.id.home_content_parent);

        sendMessageFab.setMenuButtonColorNormal(getResources().getColor(R.color.colorPrimary));
        sendMessageFab.setMenuButtonColorPressed(getResources().getColor(R.color.colorPrimaryDark));
        sendMessageFab.setMenuButtonColorRipple(getResources().getColor(R.color.colorPrimaryDark));

        emergencyMsgFab.setColorNormal(getResources().getColor(R.color.primaryLightColor));
        emergencyMsgFab.setColorPressed(getResources().getColor(R.color.colorPrimary));
        emergencyMsgFab.setColorRipple(getResources().getColor(R.color.colorPrimary));
        emergencyMsgFab.setClickable(true);

        customMsgFab.setColorNormal(getResources().getColor(R.color.primaryLightColor));
        customMsgFab.setColorPressed(getResources().getColor(R.color.colorPrimary));
        customMsgFab.setColorRipple(getResources().getColor(R.color.colorPrimary));
        customMsgFab.setClickable(true);
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


//States of a actvity and menu and navigation item selected methods*********************************


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        radius = Integer.parseInt(geoCastSettings.getRadiusSetting(con));
        super.onResume();
        Log.e("on resume", "on resume");
        navigationView.setCheckedItem(R.id.home_nav_home);
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("on pause", "on pause");
        isInFront = false;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (sendMessageFab.isOpened()) {
            sendMessageFab.close(true);
        } else {
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
            Log.e("nav_logout= ", "true");
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
                            fileHelper = new FileHelper();
                            fileHelper.deleteFile(con);

                            //set loggedIn bit in database to Zero
                            //do this on a new thread
                            new Thread(new Runnable() {
                                public void run() {                                                     //THREAD 3............
                                    // a potentially  time consuming task
                                    network = new Network(APIEndPoint.lgot, username, password, "dummy", "00.00", "00.00", "dummy", null, null, null, null);
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
        } else if (id == R.id.home_action_settings) {
            Intent i = new Intent(con, jeeryweb.geocast.Activities.Settings.class);
            con.startActivity(i);
            //finish();


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
            i.putExtra("sender", "dummy");
            i.putExtra("msg", "dummy");
            con.startActivity(i);

        } else if (id == R.id.home_nav_sent) {
            Intent i = new Intent(con, Sent.class);
            con.startActivity(i);

        } else if (id == R.id.home_nav_reliabilties) {
            // Handle the inbox........................................................................................
            //go to Reliabilties activity
            Intent i = new Intent(con, Reliabilities.class);
            con.startActivity(i);


        } else if (id == R.id.home_nav_share) {

            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "GeoCast");
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "http://geocast.in \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "choose one"));
            } catch(Exception e) {
                Log.e(TAG, "Error occurred in share");
            }

        } else if (id == R.id.home_nav_feedback) {
            startActivity(new Intent(this, Feedback.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //***************************
    private Boolean isHelpingUser(String s, Set<String> h) {

        for (String temp : h) {
            if (s.equalsIgnoreCase(temp))
                return true;
        }
        return false;
    }
}
