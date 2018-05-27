package jeeryweb.geocast.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeryweb.geocast.Adapters.SentListviewAdapter;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Models.SentRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;
import jeeryweb.geocast.databinding.ActivitySentBinding;

public class Sent extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = "Sent";
    String username,password;
    Context con;
    Activity act;
    ActivitySentBinding activitySentBinding;
    SentListviewAdapter sentListviewAdapter;
    ListView recordsList;
    NavigationView navigationView;
    APIEndPoint apiEndPoint;
    SharedPrefHandler sharedPrefHandler;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent);

        sharedPrefHandler = new SharedPrefHandler(this);
        con = this;
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

        navigationView = (NavigationView) findViewById(R.id.sent_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.inbox_nav_inbox);


        //getting navbar header items- image , name ,welcome
       View inboxNavHeader = navigationView.getHeaderView(0);
        LinearLayout navHeaderLayout = (LinearLayout) inboxNavHeader.findViewById(R.id.sent_nav_layout);
        //setting listner on nav header layout to go to MyProfile Activity
        navHeaderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to MyProfile Activity
                Intent i = new Intent(con, MyProfile.class);
                con.startActivity(i);
            }
        });

        recordsList = (ListView) findViewById(R.id.SentchatList);
        con = this;
        act = this;

        sentListviewAdapter = new SentListviewAdapter();

        final List<SentRowRecord> rows = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");
        Log.e(TAG + " retrieved session:", username + "  " + password);


        progressDialog = new ProgressDialog(Sent.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Messages...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndPoint.getAllSentMessage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(!obj.getBoolean("success")){
                                Log.e(TAG, "Error occurred suucess failed");
                                Toast.makeText(con, "Error occurred", Toast.LENGTH_SHORT).show();
                            }

                            JSONArray objArray = obj.getJSONArray("message");

                            Log.e(TAG, String.valueOf(objArray));
                            for (int i = 0; i < objArray.length(); i++) {
                                JSONObject rowobj = objArray.getJSONObject(i);

                                rows.add(new SentRowRecord(
                                        rowobj.getString("msg"),
                                        rowobj.getString("time"),
                                        rowobj.getInt("msgNo")
                                        ));
                            }
                            sentListviewAdapter.recordsInListview(con, recordsList,act , rows);
                        } catch (JSONException e) {
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
                params.put("Username", username);
                params.put("Password", password);

                return params;
            }
        };

        requestQueue.add(stringRequest);
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
        getMenuInflater().inflate(R.menu.sent, menu);
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
