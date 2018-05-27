package jeeryweb.geocast.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import jeeryweb.geocast.R;

public class UserProfileView extends AppCompatActivity {

    String username;
    int userID;
    TextView textView;
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
        textView = findViewById(R.id.userNameView);
        textView.setText(username);

    }
}
