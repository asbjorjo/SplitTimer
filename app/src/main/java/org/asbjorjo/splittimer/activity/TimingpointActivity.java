package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.DbHelper;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class TimingpointActivity extends AppCompatActivity {
    private static final String TAG = TimingpointActivity.class.getSimpleName();

    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));
        Log.d(TAG, String.format("savedInstanceState: %s",
                savedInstanceState == null ? savedInstanceState : savedInstanceState.toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timingpoint_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}