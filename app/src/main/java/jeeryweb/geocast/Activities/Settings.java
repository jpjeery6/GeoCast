package jeeryweb.geocast.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import jeeryweb.geocast.Fragments.SettingsFragment;
import jeeryweb.geocast.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment =new SettingsFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(savedInstanceState == null) {
            fragmentTransaction.add(R.id.settings_fragment_container,fragment,"settings_fragment");
            fragmentTransaction.commit();
        } else {
            fragment = getFragmentManager().findFragmentByTag("settings_fragment");
        }


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int setRadius = sharedPref.getInt(SettingsFragment.KEY_RADIUS, 0);
        boolean setSendToRelOnlySett = sharedPref.getBoolean(SettingsFragment.KEY_SEND_TO_REL_SETT, false);

        Log.e("settings radius = " , setRadius + " ");
        Log.e("settings on off = " , setSendToRelOnlySett +" ");


    }

    public String getRadiusSetting(Context con) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
        int setRadius = sharedPref.getInt(SettingsFragment.KEY_RADIUS, 0);
        boolean setSendToRelOnlySett = sharedPref.getBoolean(SettingsFragment.KEY_SEND_TO_REL_SETT, false);

        Log.e("settings radius = ", setRadius + " ");
        Log.e("settings on off = ", setSendToRelOnlySett + " ");
        return Integer.toString(setRadius);
    }

    public String getReliableSetting(Context con) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
        int setRadius = sharedPref.getInt(SettingsFragment.KEY_RADIUS, 0);
        boolean setSendToRelOnlySett = sharedPref.getBoolean(SettingsFragment.KEY_SEND_TO_REL_SETT, false);

        Log.e("settings radius = ", setRadius + " ");
        Log.e("settings on off = ", setSendToRelOnlySett + " ");
        return String.valueOf(setSendToRelOnlySett);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
