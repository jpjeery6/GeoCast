package jeeryweb.geocast.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jeeryweb.geocast.Adapters.ReliabilitiesListviewAdapter;
import jeeryweb.geocast.Constants.APIEndPoint;
import jeeryweb.geocast.Models.ReliabilitiesRowRecord;
import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.Network;

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
    static ProgressBar loadingRel;
    static ListView recordsList;
    private static Network network;
    private static Handler handler;
    private static APIEndPoint apiEndPoint;
    private static ReliabilitiesListviewAdapter reliabilitiesListviewAdapter;
    private static List<ReliabilitiesRowRecord> rowsRel = new ArrayList<>();
    private static List<ReliabilitiesRowRecord> rowsPRel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reliabilities);

        Log.e("Reliabilties","On Create");

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


        new Thread(new Runnable() {
            public void run() {
                Log.e("Reliabilties","in new threaed");
                network = new Network(APIEndPoint.getReliabilities, Home.username, Home.password, "bhbh", null, null, "ksdhfj", null, null, null, null);
                String result = network.DoWork();
                if (result != null) {
                    //Log.e(TAG, "send message main thread" + result);
                    //pass this result to UI thread by writing a message to the UI's handler
                    //we have to parse the result into two arrays rowsRel and rowsPRel
                    Message m = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("resultRelReq", result);
                    m.setData(bundle);
                    handler.sendMessage(m);
                }
            }
        }).start();


        //getting to main ui Thread*********************
        handler = new Handler(Looper.getMainLooper()) {
            Object relReqs, pRelReqs;

            @Override
            public void handleMessage(Message msg) {

                relReqs = msg.getData().get("resultRelReq");

                if (relReqs != null) {
                    //Toast.makeText(con, "Message Sent Successfully", Toast.LENGTH_LONG).show();
                    Log.e("Reliabilties","got result from network");
                    String resultList = relReqs.toString();
                    String[] listArray = resultList.split("%");
                    Log.e("listArray size","= "+String.valueOf(listArray.length));
                    for(int i= 1 ;i<listArray.length-1;i=i+2)
                    {
                        //if(Integer.parseInt(listArray[i+1]) == 0)
                            rowsRel.add(new ReliabilitiesRowRecord(listArray[i],"abc" , "abc"));
                        //else
                            rowsPRel.add(new ReliabilitiesRowRecord(listArray[i],"abc" , "abc"));
                        Log.e("listArray["+ i + "] = ",listArray[i]);
                    }
                    updateUI();

                }

            }

        };



    }

    private void updateUI() {
        if (loadingRel != null && recordsList != null && reliabilitiesListviewAdapter != null) {
            loadingRel.setVisibility(View.INVISIBLE);
            if (ARG_SECTION_NO == 1) {
                Log.e("Arg section 1","yes");
                reliabilitiesListviewAdapter.recordsInListview(this, recordsList, this, rowsRel);
            }
            else{
                Log.e("Arg section 2","yes");
                reliabilitiesListviewAdapter.recordsInListview(this, recordsList, this, rowsPRel);
        }}
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

            recordsList= (ListView)rootView.findViewById(R.id.reliabilitylist);
            loadingRel = (ProgressBar)rootView.findViewById(R.id.loadingRel);

            loadingRel.setVisibility(View.VISIBLE);

            //single network call to get everything
            reliabilitiesListviewAdapter = new ReliabilitiesListviewAdapter();

            if(ARG_SECTION_NO == 1)
                textView.setText("Reliable Users");
            else
                textView.setText("Pending Reliable Users");


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
