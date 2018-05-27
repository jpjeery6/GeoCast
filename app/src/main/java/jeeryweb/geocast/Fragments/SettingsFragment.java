package jeeryweb.geocast.Fragments;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import jeeryweb.geocast.R;

public class SettingsFragment extends PreferenceFragment{

    private CheckBoxPreference sentToRelCB;
    private SeekBar seekBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen);

        sentToRelCB =(CheckBoxPreference)findPreference("sent_only_to_rel_checkbox");


    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        View view = inflater.inflate(R.xml.settings_screen, container, false);
////        seekBar = view.f
//
//        return  view;
//    }
}
