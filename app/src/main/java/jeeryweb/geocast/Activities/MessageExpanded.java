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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.geocast.R;

public class MessageExpanded extends AppCompatActivity {

    private String TAG = "MessageExpandedClass";
    private TextView messageBodyView, messageSenderView, messageTimeView;
    private Button ackYesView, ackNoView;
    private String _lattitideSender =null, _longitudeSender=null;
    private CardView cardViewHelp;
    Context con;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        con = this;
        //widgets
        messageBodyView = (TextView)findViewById(R.id.Message_messageBody);
        // messageSenderView  = (TextView)findViewById(R.id.Message_messageSender);
        messageTimeView  = (TextView)findViewById(R.id.Message_messageTime);
        cardViewHelp = (CardView) findViewById(R.id.cardViewHelp);
        ackYesView = (Button) findViewById(R.id.Message_AckYes);
        ackNoView = (Button)findViewById(R.id.Message_AckNo);

        Intent intent =getIntent();
        int id;
        String message = null,timeSent= null, sender= null;

        //timeSent = "2018-05-05 21:20:00";  //for debugging

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

        Log.e(TAG , message);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}