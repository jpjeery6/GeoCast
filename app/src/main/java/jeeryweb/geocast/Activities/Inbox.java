package jeeryweb.geocast.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jeeryweb.geocast.Adapters.InboxListviewAdapter;
import jeeryweb.geocast.Models.InboxRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.databinding.ActivityInboxBinding;

public class Inbox extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String filename = "GeoCastInbox";
    public boolean isInFront;
    Context con;
    ActivityInboxBinding activityInboxBinding;
    InboxListviewAdapter inboxListviewAdapter;
    ListView recordsList;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.inbox_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.inbox_nav_inbox);


        //getting navbar header items- image , name ,welcome
        View inboxNavHeader=navigationView.getHeaderView(0);
        LinearLayout navHeaderLayout= (LinearLayout)inboxNavHeader.findViewById(R.id.inbox_nav_layout);
        //setting listner on nav header layout to go to MyProfile Activity
        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to MyProfile Activity
                Intent i = new Intent(con, MyProfile.class);
                con.startActivity(i);
            }
        });

        //set username in navigation drawer
        //TextView navUsername = (TextView) navHeaderView.findViewById(R.id.nav_username);
        //navUsername.setText(username.toUpperCase());

        //getting widgets
        recordsList = (ListView) findViewById(R.id.chatList);
        con=this;

        inboxListviewAdapter = new InboxListviewAdapter();

        //parts of the message
        String[] separated;

        //convert the file into a list
        List<InboxRowRecord> rows = new ArrayList<>();
        try {
            InputStream inputStream = this.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null) {
                    //here i am getting each line
                    //scan each message and separate the parts in the message
                    Log.e("lines = ",receiveString);
                    if(receiveString.contains("%")) {
                        separated = receiveString.split("%");

                        // if sender is same create only one entry in listview
                        rows.add(new InboxRowRecord(separated[0], separated[1], separated[2]));
                    }
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("File Helper", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Helper", "Can not read file: " + e.toString());
        }

        //now send the list to populate the listview
        Collections.reverse(rows);
        inboxListviewAdapter.recordsInListview(this,recordsList, this, rows);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.inbox_nav_home) {

            //go to home activity
            //NavUtils.navigateUpFromSameTask(this);

            //Intent i = new Intent(con, Home.class);
            //con.startActivity(i);
            super.onBackPressed();
            finish();


        } else if (id == R.id.inbox_nav_inbox) {


        } else if (id == R.id.inbox_nav_sent) {

        } else if (id == R.id.inbox_nav_tools) {

        } else if (id == R.id.inbox_nav_share) {

        } else if (id == R.id.inbox_nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
