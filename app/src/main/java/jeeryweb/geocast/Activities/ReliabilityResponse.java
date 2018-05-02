package jeeryweb.geocast.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class ReliabilityResponse extends AppCompatActivity {

    private static Handler handler;
    //TAG for Logging
//    private final String getPPSummary = "https://jeeryweb.000webhostapp.com/ProjectLoc/getSummary.php";
//    private final String sendreliableresconf = "https://jeeryweb.000webhostapp.com/ProjectLoc/acceptDenyReliability.php";
    private String username, phoneno;
    private TextView User, summary;
    private EditText phno;
    private TextView recievedReqUser;
    private Button reliableDeny,reliableConf;
    private ProgressBar loadingSummaryprogressBar;
    private CircleImageView pp;
    private SharedPrefHandler sharedPrefHandler;
    APIEndPoint apiEndPoint;
    private Network network;
    private String result;
    //private final String sendMsg = "https://jeeryweb.000webhostapp.com/ProjectLoc/uploadMsg.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliability_response);
        Intent intent = getIntent();
        username = intent.getStringExtra("RReqUsername");

        Log.e("ReliabiltyResusername", username);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pp = (CircleImageView) findViewById(R.id.activity_reliabilityres_image);
        User = (TextView) findViewById(R.id.activity_reliabilityres_Username);
        summary = (TextView) findViewById(R.id.activity_reliabilityres_summary);
        phno = (EditText) findViewById(R.id.activity_reliabilityres_phno);
        recievedReqUser = (TextView) findViewById(R.id.activity_reliabilityres_txt1);
        reliableDeny = (Button) findViewById(R.id.activity_reliabilityres_btndeny);
        reliableConf = (Button) findViewById(R.id.activity_reliabilityres_btnconf);

        loadingSummaryprogressBar = (ProgressBar) findViewById(R.id.activity_reliabilityres_progressbar);

        sharedPrefHandler = new SharedPrefHandler(this);

        recievedReqUser.setText(Html.fromHtml("<b>" + username + "</b> " +" has sent you a Reliability request." ));


        User.setText(username);

        if (sharedPrefHandler.getPhoneNo() != null)
            phno.setVisibility(View.GONE);

        loadingSummaryprogressBar.setVisibility(View.VISIBLE);

        //start new thread to get profile picture and summary
        //do this on a new thread
        new Thread(new Runnable() {
            public void run() {                                                 //THREAD 4.............
                // a potentially  time consuming task
                network = new Network(apiEndPoint.getPPSummary, username, "kkk", "kkk", "kk", "kk", "ksdhfj", null, null, null, null);
                result = network.DoWork();
                if (result != null) {
                    Log.e("get PP abd summary", result);
                    //pass this result to UI thread by writing a message to the UI's handler
                    Message m = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultPPSummary", result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();

        handler = new Handler(Looper.getMainLooper()) {
            Object PPSummary, reliableReq;
            String splitter;

            @Override
            public void handleMessage(Message msg) {
                PPSummary = msg.getData().get("resultPPSummary");
                if (PPSummary != null) {
                    splitter = PPSummary.toString();
                    String[] PPSum = splitter.split("<br>");

                    String PPlink = PPSum[0];
                    String Summary = PPSum[1];

                    new setPP(PPlink).execute();

                    summary.setText(Summary);
                    loadingSummaryprogressBar.setVisibility(View.GONE);

                }

                reliableReq = msg.getData().get("RResponseResult");
                if (reliableReq != null) {
                    //extract nearby users
                    splitter = reliableReq.toString();
                    if (splitter.contains("error"))
                        Toast.makeText(getApplicationContext(), "Can't send response right now", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(getApplicationContext(), "Reliability response send", Toast.LENGTH_SHORT).show();
                        finish();
                        //onBackPressed();
                    }
                }
            }
        };


        //done-----------
        reliableDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResponse("no");
            }
        });


        reliableConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneno = phno.getText().toString();
                if (true) {

                    //checkPhoneNumberValid()
                    //start new thraed to send a message notification to the user
                    //get the firebase token of the user
                    //send a req to firebase server to push a message
                    sendResponse("yes");

                }

            }
        });

    }

    private void sendResponse(final String msgres)
    {
        //do this on a new thread
        new Thread(new Runnable() {
            public void run() {                                                 //THREAD 4.............
                // a potentially  time consuming task
                network = new Network(apiEndPoint.sendreliableresconf, Home.username, username, msgres, "kk", "kk", "ksdhfj", null, null, phoneno, null);
                result = network.DoWork();
                if (result != null) {
                    Log.e("send reliability res", result);
                    //pass this result to UI thread by writing a message to the UI's handler
                    Message m = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("RResponseResult", result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }

            }
        }).start();
    }


    private boolean checkPhoneNumberValid() {
        boolean valid = true;
        String p = phno.getText().toString();
        Log.e("phno input", p + "len=" + String.valueOf(p.length()));
        if (p.length() != 10 || p.length() == 0) {
            phno.setError("Invalid phone number");
            valid = false;
        } else {
            phno.setError(null);
        }


        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class setPP extends AsyncTask<Void, Void, Bitmap> {
        private String name;

        public setPP(String name) {
            this.name = name;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                java.net.URL url = new java.net.URL(name);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }


        protected void onPostExecute(Bitmap b) {
            //show image uploaded
            if (b != null)
                pp.setImageBitmap(b);
            else
                Toast.makeText(getApplicationContext(), "Can't get User's Profile picture", Toast.LENGTH_SHORT).show();
        }
    }
}


