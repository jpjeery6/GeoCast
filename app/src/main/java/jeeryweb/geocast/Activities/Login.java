package jeeryweb.geocast.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.ImeiExtractor;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Login extends AppCompatActivity {

//Attributes****************************************************************************************

    //Objects
    APIEndPoint apiEndPoint;
    Network network;
    SharedPrefHandler session;
    ImeiExtractor imeiExtractor;
    Context c;
    Handler handler;

    private final String TAG = getClass().getSimpleName() + " LoginActivity";
//    private final String lgIn = "https://jeeryweb.000webhostapp.com/ProjectLoc/login.php";
//    private final String migL = "https://jeeryweb.000webhostapp.com/ProjectLoc/migrate.php";

    //widgets
    EditText user, pass;
    Button login;
    TextView registerLink,migrate;
    ProgressDialog progressDialog;

    public String us = null, pa = null, result, imeinumber;

//Methods*******************************************************************************************
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //setting up status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //creating sessions, loginregister objects and context
        session = new SharedPrefHandler(this);
        imeiExtractor = new ImeiExtractor(this);
        c = this;

        //setting up widgets
        user = (EditText) findViewById(R.id.activity_login_user);
        pass = (EditText) findViewById(R.id.activity_login_pass);
        login = (Button) findViewById(R.id.activity_login_loginbtn);
        registerLink = (TextView) findViewById(R.id.activity_login_link_signup);
        migrate = (TextView) findViewById(R.id.activity_login_migartebtn);


        if (session.isLoggedIn()) {

//IF USER IS LOGGED IN IN THE SHARED PREFERENCES GOT TO HOME ACTIVITY DIRECTLY
            Intent i = new Intent(c, Home.class);

            Bundle bundle = new Bundle();
            bundle.putBoolean("firstTime", false);
            i.putExtras(bundle);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            finish();
        } else {
//IF NOT CHECK IF USER HAS LOGGED IN EARLIER FROM THIS DEVICE
            //check if there is a shared preference entry if yes load it
            //Retrieving username and password
            HashMap<String, String> userMap = session.getUserDetails();
            if (userMap != null) {
                String rusername = userMap.get("name");
                String rpassword = userMap.get("pass");
                Log.e(TAG + "retrieved", rusername + "  " + rpassword);
                user.setText(rusername);
                pass.setText(rpassword);
            }
        }


        //new listeners-------------------
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        migrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                migrateAccount();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the register activity
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });


        //Handler for communication between UI and login/ register thread
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                result = msg.getData().get("result").toString();
                //Toast.makeText(c, result, Toast.LENGTH_LONG).show();

                if (progressDialog != null)
                    progressDialog.dismiss();

                // Starting nextActivity
                if (result.contains("migrate")) {
                    Toast.makeText(Login.this, "Please Migrate account", Toast.LENGTH_SHORT).show();
                } else if (result.contains("credentials")) {
                    Toast.makeText(Login.this, "Your credentials are wrong", Toast.LENGTH_SHORT).show();
                } else if (result.contains("valid")) {

                    session.createLoginSession(us, pa);

                    Intent i = new Intent(c, Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("firstTime", true);
                    i.putExtras(bundle);
                    //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };
        runtime_permissions();

    }



    //added two extra permissions for pushy**********************************************
    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                //do nothing
            } else {
                runtime_permissions();
            }
        }
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }
        //valid input is recieved
        if(session.getIMEI()==null)
            imeinumber = imeiExtractor.getPhoneIEMINumber();
        else
            imeinumber = session.getIMEI();

        Log.e(TAG, "imeinumber is "+imeinumber);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        us = user.getText().toString();
        pa = pass.getText().toString();

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                network = new Network(apiEndPoint.lgIn, us, pa, "dummymsg", "00.00", "00.00", "jhdjhjjjkh", imeinumber, null, null, null);
                result = network.DoWork();

                if (result != null) {
                    //pass this result to UI thread by writing a message to the UI's handler on line 107
                    Message m = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();
    }


    public boolean validate() {
        boolean valid = true;

        String email = user.getText().toString();
        String password = pass.getText().toString();

        if (email.isEmpty() || email.length() < 4 || email.length() > 20) {
            user.setError("enter a valid username between 4 to 20 characters");
            valid = false;
        } else {
            user.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            pass.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    public void migrateAccount() {
        us = user.getText().toString();
        pa = pass.getText().toString();
        if (us == null || pa == null) {
            user.setError("Enter username");
            pass.setError("Enter Password");
            return;
        }
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Migrating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //this will set New_imei
        final String new_imei = imeiExtractor.getPhoneIEMINumber();

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                network = new Network(apiEndPoint.migL, us, pa, "dummymsg", "00.00", "00.00", "jhdjhjjjkh", new_imei, null, null, null);
                result = network.DoWork();


                if (result.contains("valid")) {
                    session.saveIMEI(new_imei);
                    Message m = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("result", result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                } else if (result.contains("credentials")) {
                    Toast.makeText(getBaseContext(), "Wrong credentials", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Migration Failed", Toast.LENGTH_LONG).show();
                }

            }
        }).start();
    }


    public String getPhoneIEMINumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            IMEI = telephonyManager.getDeviceId();
        } catch (Exception e) {
            IMEI = "Error!";
        }
        return IMEI;
    }
}
