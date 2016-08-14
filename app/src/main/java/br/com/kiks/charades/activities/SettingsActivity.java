package br.com.kiks.charades.activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.common.base.Joiner;
import com.kiks.charades.R;

import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PrefsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            Map<String, ?> prefs = getPreferenceManager().getSharedPreferences().getAll();
            for (String key : prefs.keySet()) {
                Preference pref = findPreference(key);
                if (pref != null) {
                    updateSummary(pref);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager()
                    .getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager()
                    .getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference pref = findPreference(key);
            updateSummary(pref);
        }

        private void updateSummary(Preference pref) {
            if (pref instanceof ListPreference) {
                ListPreference listPref = (ListPreference)pref;
                pref.setSummary(listPref.getEntry());
            }
            if (pref instanceof MultiSelectListPreference) {
                MultiSelectListPreference multiPref = (MultiSelectListPreference)pref;
                pref.setSummary(Joiner.on(", ").join(multiPref.getEntries()));
            }
        }
    }
}
