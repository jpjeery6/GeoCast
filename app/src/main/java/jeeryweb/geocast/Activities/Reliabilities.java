package jeeryweb.geocast.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeeryweb.geocast.Adapters.ReliabilitiesListviewAdapter;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Models.ReliabilitiesRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.Network;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class Reliabilities extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    static View rootView;
    static int ARG_SECTION_NO;
    static ListView recordsList1,recordsList2;
    private static Network network;
    private static Handler handler;
    private static APIEndPoint apiEndPoint;
    private SharedPrefHandler sharedPrefHandler;
    private static ReliabilitiesListviewAdapter reliabilitiesListviewAdapter;
    private static List<ReliabilitiesRowRecord> rowsRel = new ArrayList<>();
    private static List<ReliabilitiesRowRecord> rowsPRel = new ArrayList<>();
    final String TAG = "Reliabilties";
    Context con;
    String username, password;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliabilities);

        Log.e(TAG,"On Create");
        con = this;
        sharedPrefHandler = new SharedPrefHandler(con);
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));


        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_reliabilities_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabindicator);
        tabLayout.setupWithViewPager(mViewPager, true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        HashMap<String, String> user = sharedPrefHandler.getUserDetails();
        username = user.get("name");
        password = user.get("pass");

        progressDialog = new ProgressDialog(Reliabilities.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        rowsPRel.clear();
        rowsRel.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiEndPoint.getReliableConnections,
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

                                if(rowobj.getInt("RStatus")==1){
                                    rowsRel.add(new ReliabilitiesRowRecord(
                                            rowobj.getString("user"),
                                            rowobj.getString("picture"),
                                            rowobj.getInt("userID"),
                                            rowobj.getInt("RStatus")
                                    ));
                                }
                                if(rowobj.getInt("RStatus")==0){
                                    rowsPRel.add(new ReliabilitiesRowRecord(
                                            rowobj.getString("user"),
                                            rowobj.getString("picture"),
                                            rowobj.getInt("userID"),
                                            rowobj.getInt("RStatus")
                                    ));
                                }
                            }
                            Collections.reverse(rowsPRel);
                            Collections.reverse(rowsRel);

                            Log.e(TAG, "rowsPrel "+String.valueOf(rowsPRel));
                            Log.e(TAG, "rowsRel "+String.valueOf(rowsRel));
                            updateUI(1);
                            updateUI(2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error "+e);
                            Toast.makeText(con, "Response error from server", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG,error.getMessage());
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

    private void updateUI(int id) {
        if (recordsList1 != null && reliabilitiesListviewAdapter != null) {
            if (id == 1) {
                Log.e("Arg section 1","yes");
                reliabilitiesListviewAdapter.recordsInListview(this, recordsList1, this, rowsRel);
            }
            else{
                Log.e("Arg section 2","yes");
                reliabilitiesListviewAdapter.recordsInListview(this, recordsList2, this, rowsPRel);
        }}
    }
    public void loadAllData(){

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reliabilities, menu);
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

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Log.e("Reliabilties","On create view");

            final String result;


            rootView = inflater.inflate(R.layout.fragment_reliabilities, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            ARG_SECTION_NO = getArguments().getInt(ARG_SECTION_NUMBER);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            if(ARG_SECTION_NO==1)
              recordsList1= (ListView)rootView.findViewById(R.id.reliabilitylist);
            else
              recordsList2= (ListView)rootView.findViewById(R.id.reliabilitylist);

            //single network call to get everything
            reliabilitiesListviewAdapter = new ReliabilitiesListviewAdapter();

            if(ARG_SECTION_NO == 1){
                textView.setText("Reliable Users");
            }

            else{
                textView.setText("Pending Reliable Users");
            }



            return rootView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
