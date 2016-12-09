package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.asbjorjo.splittimer.fragment.SettingsFragment;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = PreferenceActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("SettingsActivity", "onCreate");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
