package jeeryweb.geocast.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Login extends AppCompatActivity {

//Attributes*******************************************************************

    //Objects
    private Network network;
    private SharedPrefHandler sharedPrefHandler;
    private Context c;
    private Handler handler;



    //widgets
    private EditText usernameField , passwordField;
    private Button loginButton ;
    private TextView registerLinkText;
    private ProgressDialog progressDialog;


    //Variables
    private final String TAG=getClass().getSimpleName()+"Activity ";
    private String us,pa,result;


//Methods***********************************************************************
//Method 1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //setting up status bar
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        //creating sessions, loginregister objects and context
        sharedPrefHandler = new SharedPrefHandler(this);
        c = this;

        //setting up widgets
        usernameField=(EditText)findViewById(R.id.activity_login_username);
        passwordField=(EditText)findViewById(R.id.activity_login_password);
        loginButton=(Button)findViewById(R.id.activity_login_loginbutton) ;
        registerLinkText=(TextView)findViewById(R.id.activity_login_linksignup);



        if(sharedPrefHandler.isLoggedIn())
        {
            Intent i = new Intent(c, Home.class);

            Bundle bundle = new Bundle();
            bundle.putBoolean("firstTime",false);
            i.putExtras(bundle);

            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            finish();
        }
        else
        {
            //check if there is a shared preference entry if yes load it
            //Retrieving username and password
            HashMap<String, String> userMap = sharedPrefHandler.getUserDetails();
            if(userMap!=null) {
                String rusername = userMap.get("name");
                String rpassword = userMap.get("pass");
                Log.e(TAG + "retrieved", rusername + "  " + rpassword);
                usernameField.setText(rusername);
                passwordField.setText(rpassword);
            }
        }


        //setting up listeners-------------------
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the register activity
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });


        //Handler for communication between UI and login/ register thread
        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                result = msg.getData().get("result").toString();
                Toast.makeText(c, result, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                //login.setEnabled(false);
                // Starting nextActivity
                if (result.contains("valid")) {

                    sharedPrefHandler.createLoginSession(us, pa);

                    Intent i = new Intent(c, Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("firstTime",true);
                    i.putExtras(bundle);
                    //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            }
        };
        runtime_permissions();
    }


//Method 2
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


//Method 3
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //do nothing
            } else {
                runtime_permissions();
            }
        }
    }


//Method 4
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            return;
        }
        //valid input is recieved
        //login.setEnabled(true);

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        us = usernameField.getText().toString();
        pa = passwordField.getText().toString();

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                network =new Network(getString(R.string.network_login),us,pa,"dummymsg","00.00","00.00","jhdjhjjjkh");
                result=network.DoWork();

                if(result!=null) {
                    //pass this result to UI thread by writing a message to the UI's handler on line 107
                    Message m=Message.obtain();
                    Bundle bundle=new Bundle();
                    bundle.putString("result",result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();
    }


//Method 5
    public boolean validate() {
        boolean valid = true;

        String email = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || email.length() < 4 || email.length() > 20) {
            usernameField.setError("enter a valid username between 4 to 20 characters");
            valid = false;
        } else {
            usernameField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordField.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }


}
