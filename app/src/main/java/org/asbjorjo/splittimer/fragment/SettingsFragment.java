package org.asbjorjo.splittimer.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.asbjorjo.splittimer.R;

/**
 * Created by AJohansen2 on 12/4/2016.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
