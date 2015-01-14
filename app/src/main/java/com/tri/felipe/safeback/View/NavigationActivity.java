package com.tri.felipe.safeback.View;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.tri.felipe.safeback.R;


public class NavigationActivity extends Activity {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private SkeletonFragment sf = new SkeletonFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, sf)
                .commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }
}
