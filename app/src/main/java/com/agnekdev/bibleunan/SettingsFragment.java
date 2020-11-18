package com.agnekdev.bibleunan;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import utilities.Functions;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat mModeNight;
    private SeekBarPreference sbp;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModeNight = findPreference("mode_night");
        sbp = findPreference("audio_rate");


        mModeNight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isNightMode =(boolean)newValue;
                AppCompatDelegate
                        .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            }
        });

        sbp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Functions.agnekLog(newValue.toString());
                return true;
            }
        });
    }
}
