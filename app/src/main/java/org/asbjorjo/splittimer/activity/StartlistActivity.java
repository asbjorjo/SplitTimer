package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.fragment.StartlistEditFragment;
import org.asbjorjo.splittimer.fragment.StartlistFragment;
import org.asbjorjo.splittimer.model.Event;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class StartlistActivity extends AppCompatActivity implements
        StartlistEditFragment.OnStartlistEntryAddedListener {
    private static final String TAG = StartlistActivity.class.getSimpleName();

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

        Event event = getIntent().getParcelableExtra(KEY_ACTIVE_EVENT);

        StartlistFragment sf = StartlistFragment.newInstance(event);
        StartlistEditFragment sef = StartlistEditFragment.newInstance(event);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.startlist_edit, sef);
        ft.replace(R.id.startlist, sf);
        ft.commit();

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onStartListEntryAdded(Event event, long athleteId) {
        StartlistFragment sf = (StartlistFragment) getSupportFragmentManager().
                findFragmentById(R.id.startlist);
        sf.refreshData();

        setResult(RESULT_OK);
    }
}