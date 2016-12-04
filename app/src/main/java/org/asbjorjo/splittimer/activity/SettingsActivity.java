package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.asbjorjo.splittimer.fragment.SettingsFragment;

/**
 * Created by AJohansen2 on 12/4/2016.
 */

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("SettingsActivity", "onCreate");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
