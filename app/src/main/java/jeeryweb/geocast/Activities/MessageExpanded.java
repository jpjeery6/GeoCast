package jeeryweb.geocast.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import jeeryweb.geocast.R;

public class MessageExpanded extends AppCompatActivity {

    private String TAG = "MessageExpandedClass";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_expanded);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent =getIntent();

        int id = intent.getIntExtra("id", 0);
        String message = intent.getStringExtra("message");
        boolean success = intent.getBooleanExtra("success", false);

        Log.e(TAG , message);

    }

}
