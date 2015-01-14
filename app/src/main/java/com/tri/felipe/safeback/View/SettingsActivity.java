package com.tri.felipe.safeback.View;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.tri.felipe.safeback.R;

/**
 * Created by Felipe on 14-12-28.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.title_activity_settings);
        // Display the fragment as the main content.
        addPreferencesFromResource(R.xml.preferences);
    }
}
