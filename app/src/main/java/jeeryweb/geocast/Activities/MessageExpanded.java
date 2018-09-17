package jeeryweb.geocast.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class MessageExpanded extends AppCompatActivity {

    private String TAG = "MessageExpandedClass";
    private CircleImageView ppSender;
    private TextView messageBodyView, messageSenderView, messageTimeView;
    private Button ackYesView, ackNoView;
    private String _lattitideSender =null, _longitudeSender=null;
    private CardView cardViewHelp;
    Context con;
    String sender;
    String PPlink;
    RequestQueue requestQueue;
    SharedPrefHandler sharedPrefHandler;
    // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    int id;
    String message = null, timeSent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);

        requestQueue = Volley.newRequestQueue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        con = this;
        sharedPrefHandler = new SharedPrefHandler(con);
        //widgets
        ppSender = findViewById(R.id.Message_expanded_profile_image);
        messageBodyView = findViewById(R.id.Message_messageBody);
        messageSenderView = findViewById(R.id.Message_Expanded_senderId);
        messageTimeView = findViewById(R.id.Message_messageTime);
        cardViewHelp = findViewById(R.id.cardViewHelp);
        ackYesView = findViewById(R.id.Message_AckYes);
        ackNoView = findViewById(R.id.Message_AckNo);

        Intent intent =getIntent();


        //timeSent = "2018-05-05 21:20:00";  //for debugging

        Log.e("On create running","Message Expanded calss");

        if(intent.hasExtra("msg"))
            message = intent.getStringExtra("msg");

        if(intent.hasExtra("time"))
            timeSent = intent.getStringExtra("time");

        if(intent.hasExtra("sender"))
            sender = intent.getStringExtra("sender");

        if(intent.hasExtra("latti"))
            _lattitideSender = intent.getStringExtra("latti");
        if(intent.hasExtra("longi"))
            _longitudeSender = intent.getStringExtra("longi");

//        if(sender!=null)
//            getSupportActionBar().setTitle(sender);
//        else


        Log.e(TAG , message);

        messageBodyView.setText(message);

        messageSenderView.setText(sender);
        messageSenderView.setTextSize(6 * getResources().getDisplayMetrics().density);
        //messageBodyView.setTextSize(6 * getResources().getDisplayMetrics().density);
        messageTimeView.setText(timeSent);

        Log.e(TAG, timeSent);
        Boolean isMessageOld = _isMessageOld(timeSent);

        if(isMessageOld)
            disableAckWidgets();




        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.getPPSummary,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                                Toast.makeText(con, "Got Image Url", Toast.LENGTH_SHORT).show();
                                String[] PPSum = response.split("<br>");
                                PPlink = PPSum[0];
                                Log.e("Volley Img Msg Exp"," " + PPlink);
                                getPPwithVolley();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error occurred parse error");
                            Toast.makeText(con, "Response error from server", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Username", sender);

                return params;
            }
        };

        requestQueue.add(stringRequest);



        ackNoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ackYesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_lattitideSender==null  ||  _longitudeSender==null){
                    Toast.makeText(con, "Error ooccureed!", Toast.LENGTH_SHORT).show();
                    return;
                }


                acknoledgeSender(calculateResponseTime());
                String urlSender ="google.navigation:q="+_lattitideSender+","+_longitudeSender;
                Log.e(TAG, "urlSender "+urlSender);
                Uri gmmIntentUri = Uri.parse(urlSender);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

    }

    private void getPPwithVolley()
    {
        ImageRequest ir = new ImageRequest(PPlink,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.e(TAG, "Recieved PP image with volley");
                        ppSender.setImageBitmap(response);
                    }
                }, 0, 0, null, null);
        requestQueue.add(ir);
    }

    private long calculateResponseTime() {

        Date MessageTime = null;
        try {
            MessageTime = Home.dateFormat.parse(timeSent);
            Date currTime = new Date();

            long diff = currTime.getTime() - MessageTime.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            Log.e(TAG, "Diff is m=" + minutes);
            return minutes;
        } catch (ParseException e) {
            Log.e(TAG, "could not parse MessageTime");
            e.printStackTrace();
            return -1;
        }

    }


    Boolean _isMessageOld(String timeSent) {

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss A");
        Date MessageTime = null;
        Log.e(TAG, String.valueOf(timeSent));
        long diff, seconds, minutes = 60;  //set to 1 hour
        try {
            MessageTime = Home.dateFormat.parse(timeSent);
            Date currTime = new Date();


            Log.e(TAG, String.valueOf(MessageTime));


            diff = currTime.getTime() - MessageTime.getTime();

            seconds = diff / 1000;
            minutes = seconds / 60;
            Log.e(TAG, "Diff is m=" + minutes);

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "could not parse MessageTime");
            Toast.makeText(con, "Som error occurred with!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        return minutes > 30;
    }

    void disableAckWidgets(){
        ackNoView.setEnabled(false);
        ackYesView.setEnabled(false);
        cardViewHelp.setVisibility(View.GONE);
    }

    public void acknoledgeSender(final long responseTime) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APIEndPoint.msgAck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Your response is send to the sender of the message!", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> user = sharedPrefHandler.getUserDetails();
                String username = user.get("name");
                String password = user.get("pass");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Username", username);
                params.put("Password", password);
                params.put("ResponseTime", String.valueOf(responseTime));
                params.put("ackTime", Home.dateFormat.format(new Date()));
                params.put("sender", sender);


                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}