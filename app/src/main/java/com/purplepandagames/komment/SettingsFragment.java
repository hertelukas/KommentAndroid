package com.purplepandagames.komment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import java.util.Map;

public class SettingsFragment extends PreferenceFragmentCompat {

    public MainActivity main;
    public SharedPreferences settings;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);

        final CheckBoxPreference confirm_delete = findPreference("confirm_delete");
        final Preference logout = findPreference("logout");

        if(confirm_delete != null){
            confirm_delete.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.confirmDelete = (Boolean)newValue;
                    Log.i("Confirm Delete", "onPreferenceChange: " + (Boolean)newValue);
                    return true;
                }
            });
        }

        if(logout != null){
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MainActivity main = (MainActivity) getActivity();
                    if(main != null){
                        main.Logout();
                    }
                    Log.i("TAG", "onPreferenceChange: Logging out" );
                    return true;
                }
            });

        }


    }

}
