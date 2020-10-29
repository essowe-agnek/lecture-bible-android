package com.agnekdev.planlecturebible;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Calendar;

import utilities.Functions;

public class SettingsFragment extends PreferenceFragmentCompat implements TimePickerDialog.OnTimeSetListener {
    private Preference btnTimeFilter;
    private SwitchPreferenceCompat mModeNight;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnTimeFilter =findPreference("btnTimeFilter");
        mModeNight = findPreference("mode_night");

        String timeSet =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("time_not","05:00");
        btnTimeFilter.setSummary(timeSet);

        btnTimeFilter.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimeDialog();
                return false;
            }
        });

        mModeNight.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isNightMode =(boolean)newValue;
                AppCompatDelegate
                        .setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
                return true;
            }
        });
    }

    private void showTimeDialog(){
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(getActivity(),this,hour,minute,true).show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String timeNoti= String.format("%02d:%02d",hour,minute);
        btnTimeFilter.setSummary(timeNoti);
        boolean committed =
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putString("time_not", timeNoti).commit();
    }
}
