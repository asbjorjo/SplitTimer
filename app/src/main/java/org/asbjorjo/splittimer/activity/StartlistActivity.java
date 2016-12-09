package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.fragment.StartlistEditFragment;
import org.asbjorjo.splittimer.fragment.StartlistFragment;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class StartlistActivity extends AppCompatActivity implements
        StartlistEditFragment.OnStartlistEntryAddedListener {
    private static final String TAG = StartlistActivity.class.getSimpleName();
    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));
        Log.d(TAG, String.format("savedInstanceState: %s",
                savedInstanceState == null ? savedInstanceState : savedInstanceState.toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.startlist_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStartListEntryAdded(long eventId, long athleteId) {
        StartlistFragment sf = (StartlistFragment) getSupportFragmentManager().
                findFragmentById(R.id.startlist);
        sf.refreshData();

        setResult(RESULT_OK);
    }
}