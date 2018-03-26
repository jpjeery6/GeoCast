package jeeryweb.geocast.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

import jeeryweb.geocast.Utility.Network;

import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Register extends AppCompatActivity {


//Attributes*************************************************************

    //objects
    private Network network;
    private Context c;
    private SharedPrefHandler sharedPrefHandler;
    private Handler handler;

    //widgets
    private EditText usernameField , pass1Field,pass2Field;
    private Button registerButton ;
    private TextView loginLinkText;
    private ProgressDialog progressDialog;

    //Variables
    private final String TAG=getClass().getSimpleName()+"Activity";
    private String us,pa,result;



//Methods****************************************************************
//Method 1
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //setting up status bar
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        sharedPrefHandler = new SharedPrefHandler(this);
        c = this;

        //setting up widgets
        usernameField=(EditText)findViewById(R.id.activity_register_username);
        pass1Field=(EditText)findViewById(R.id.activity_register_password1);
        pass2Field=(EditText)findViewById(R.id.activity_register_password2);
        registerButton=(Button)findViewById(R.id.activity_register_regButton) ;
        loginLinkText=(TextView)findViewById(R.id.activity_register_linklogin);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLinkText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                result = msg.getData().get("result").toString();
                Toast.makeText(c, result, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

                // Starting nextActivity
                if (result.contains("valid")) {

                    sharedPrefHandler.createLoginSession(us, pa);
                    Intent i = new Intent(c, Home.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("firstTime",true);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            }
        };

    }

//Method 2
    public void signup() {
        Log.e(TAG, "Signup");

        if (!validate()) {
            return;
        }

        progressDialog = new ProgressDialog(Register.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();



        us = usernameField.getText().toString();
        pa = pass1Field.getText().toString();
        String reEnterPassword = pass2Field.getText().toString();

        new Thread(new Runnable() {
            public void run() {
                // a potentially  time consuming task
                network =new Network(getString(R.string.network_register),us,pa,"dummymsg","00.00","00.00","kjdk");
                result=network.DoWork();

                if(result!=null) {
                    //pass this result to UI thread by writing a message to the UI's handler
                    Message m=Message.obtain();
                    Bundle bundle=new Bundle();
                    bundle.putString("result",result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();

    }


//Method 3
    public boolean validate() {
        boolean valid = true;

        String email = usernameField.getText().toString();
        String password = pass1Field.getText().toString();
        String passwordre= pass2Field.getText().toString();

        if (email.isEmpty() || email.length() < 4 || email.length() > 20) {
            usernameField.setError("enter a valid username between 4 to 20 characters");
            valid = false;
        } else {
            usernameField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            pass1Field.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            pass2Field.setError(null);
        }

        if (passwordre.isEmpty() || passwordre.length() < 4 || passwordre.length() > 10 || !(passwordre.equals(password))) {
            pass2Field.setError("Password Do not match");
            valid = false;
        } else {
            pass2Field.setError(null);
        }

        return valid;
    }
}
