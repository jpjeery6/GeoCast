package jeeryweb.geocast.Fragments;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.pavelsikun.seekbarpreference.SeekBarPreference;

import jeeryweb.geocast.R;
import jeeryweb.geocast.Utility.SharedPrefHandler;

public class SettingsFragment extends PreferenceFragment{

    private CheckBoxPreference sentToRelCB;
    private SeekBarPreference seekBarPreference;
    private SharedPrefHandler sharedPrefHandler;

    public static final String KEY_SEND_TO_REL_SETT = "sent_only_to_rel_checkbox";
    public static final String KEY_RADIUS = "radius_pref_key";




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen);

        sentToRelCB =(CheckBoxPreference)findPreference(KEY_SEND_TO_REL_SETT);
        seekBarPreference = (SeekBarPreference) findPreference(KEY_RADIUS);


    }



}
