package jeeryweb.geocast.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import jeeryweb.geocast.R;

public class MessageExpanded extends AppCompatActivity {

    private String TAG = "MessageExpandedClass";
    private String sender , msg ,time ,latt,longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        sender = intent.getStringExtra("sender");
        msg = intent.getStringExtra("msg");
        time = intent.getStringExtra("time");

        //Log.e(TAG , message);

    }

}
