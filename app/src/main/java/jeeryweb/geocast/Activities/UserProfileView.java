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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class UserProfileView extends AppCompatActivity {


    private static Handler handler;
    private String username;
    private int userID;
    private TextView User, summary;
    private ProgressBar loadingSummaryprogressBar;
    private CircleImageView pp;
    private SharedPrefHandler sharedPrefHandler;
    APIEndPoint apiEndPoint;
    private Network network;
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);

        Intent i = getIntent();


        if(i.hasExtra("Username")){
            username = i.getStringExtra("Username");
        }

        if(i.hasExtra("userID")){
            userID = i.getIntExtra("userID", -1);
        }



        Log.e("ReliabiltyResusername", username);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pp = findViewById(R.id.activity_user_profile_view_image);
        User = findViewById(R.id.activity_user_profile_view_Username);
        summary = findViewById(R.id.activity_user_profile_view_summary);
        loadingSummaryprogressBar = findViewById(R.id.activity_user_profile_view_progressbar);

        sharedPrefHandler = new SharedPrefHandler(this);




        User.setText(username);


        loadingSummaryprogressBar.setVisibility(View.VISIBLE);

        //start new thread to get profile picture and summary
        //do this on a new thread
        new Thread(new Runnable() {
            public void run() {                                                 //THREAD 4.............
                // a potentially  time consuming task
                network = new Network(APIEndPoint.getPPSummary, username, "kkk", "kkk", "kk", "kk", "ksdhfj", null, null, null, null);
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
                    String responseTime = PPSum[2];

                    if (Double.parseDouble(responseTime) == -1)
                        responseTime = "Not responded to a message yet";
                    else
                        responseTime = responseTime + "s";

                    new UserProfileView.setPP(PPlink).execute();

                    //format the summary
                    String[] summarysplitter = Summary.split("\\|");
                    String age = summarysplitter[0];
                    String profession = summarysplitter[1];
                    String gender = summarysplitter[2];
                    //  String noConnections = summarysplitter[3];
                    summary.setText("Age: " + age + "\n" + "Profession: " + profession + "\n" + "Gender: " + gender + "\n" + "Avg Response Time: " + responseTime);
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
