package jeeryweb.geocast.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class MessageExpanded extends AppCompatActivity {

    private String TAG = "MessageExpandedClass";
    private TextView messageBodyView, messageSenderView, messageTimeView;
    private Button ackYesView, ackNoView;

    private CardView cardViewHelp;
    Context con;
    RequestQueue requestQueue;
    public APIEndPoint apiEndPoint;
    public SharedPrefHandler sharedPrefHandler;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");

    //message info
    private String message = null,timeSent= null;
    //sender info
    private String sender= null;
    private String _lattitideSender =null, _longitudeSender=null;

    //recepient info
    private String username, password;
    private long responsePeriod;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        con = this;
        sharedPrefHandler = new SharedPrefHandler(con);
        requestQueue = Volley.newRequestQueue(con);
        //widgets
        messageBodyView = (TextView)findViewById(R.id.Message_messageBody);
        // messageSenderView  = (TextView)findViewById(R.id.Message_messageSender);
        messageTimeView  = (TextView)findViewById(R.id.Message_messageTime);
        cardViewHelp = (CardView) findViewById(R.id.cardViewHelp);
        ackYesView = (Button) findViewById(R.id.Message_AckYes);
        ackNoView = (Button)findViewById(R.id.Message_AckNo);

        Intent intent =getIntent();
        int id;



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

        if(sender!=null)
            getSupportActionBar().setTitle(sender);
        else
            getSupportActionBar().setTitle(getString(R.string.title_activity_message_expanded));

       // timeSent = "2018-05-26 09:30:00 AM";  //for debugging
        Log.e(TAG , message+" "+ timeSent);

        messageBodyView.setText(message);
        messageBodyView.setTextSize(12 * getResources().getDisplayMetrics().density);
//        messageSenderView.setText(sender);
//        messageSenderView.setTextSize(20 * getResources().getDisplayMetrics().density);
        messageTimeView.setText(timeSent);
        messageTimeView.setTextSize(8 * getResources().getDisplayMetrics().density);
        Boolean isMessageOld = _isMessageOld(timeSent);

        if(isMessageOld)
            disableAckWidgets();

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
                doNetworkAck();

                String urlSender ="google.navigation:q="+_lattitideSender+","+_longitudeSender;
                Log.e(TAG, "urlSender "+urlSender);
                Uri gmmIntentUri = Uri.parse(urlSender);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

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
    void doNetworkAck(){


        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");

        final Date _send;
        Date _responseDate = new Date();
        try {
            _send = dateFormat.parse(timeSent);
            responsePeriod = (_responseDate.getTime()  - _send.getTime())/(1000*60);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String ackTime = dateFormat.format(new Date());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndPoint.msgAck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("success")){
                                Log.e(TAG, "Error occurred suucess failed");
                                Toast.makeText(con, "Error occurred", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error occurred parse error");
                            //Toast.makeText(con, "Response error from server", Toast.LENGTH_SHORT).show();
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
                params.put("Username", username);
                params.put("Password", password);
                params.put("ResponseTime", String.valueOf(responsePeriod));
                params.put("ackTime",ackTime);

                //sender
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