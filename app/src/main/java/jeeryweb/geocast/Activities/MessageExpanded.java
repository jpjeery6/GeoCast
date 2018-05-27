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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;

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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);

        requestQueue = Volley.newRequestQueue(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Message");
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        con = this;
        //widgets
        ppSender = (CircleImageView)findViewById(R.id.Message_expanded_profile_image);
        messageBodyView = (TextView)findViewById(R.id.Message_messageBody);
        messageSenderView  = (TextView)findViewById(R.id.Message_Expanded_senderId);
        messageTimeView  = (TextView)findViewById(R.id.Message_messageTime);
        cardViewHelp = (CardView) findViewById(R.id.cardViewHelp);
        ackYesView = (Button) findViewById(R.id.Message_AckYes);
        ackNoView = (Button)findViewById(R.id.Message_AckNo);

        Intent intent =getIntent();
        int id;
        String message = null,timeSent= null;

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




    Boolean _isMessageOld(String timeSent){

        Date MessageTime=null;
        Log.e(TAG, String.valueOf(timeSent));
        try {
            MessageTime = dateFormat.parse(timeSent);
        } catch (ParseException e) {
            Log.e(TAG, "could not parse MessageTime");
            e.printStackTrace();
        }
        Date currTime = new Date();
        Log.e(TAG, String.valueOf(MessageTime));

        long diff = currTime.getTime() - MessageTime.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        Log.e(TAG, "Diff is m="+minutes);

        if(minutes>30)
            return true;
        else
            return false;
    }

    void disableAckWidgets(){
        ackNoView.setEnabled(false);
        ackYesView.setEnabled(false);
        cardViewHelp.setVisibility(View.GONE);
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