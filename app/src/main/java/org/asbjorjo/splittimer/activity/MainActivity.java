package org.asbjorjo.splittimer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;
import org.asbjorjo.splittimer.fragment.EventSelectFragment;
import org.asbjorjo.splittimer.model.Event;

import static org.asbjorjo.splittimer.SplitTimerConstants.ADD_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.BUILD_INTERMEDIATES;
import static org.asbjorjo.splittimer.SplitTimerConstants.BUILD_STARTLIST;
import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.PREFS_NAME;
import static org.asbjorjo.splittimer.SplitTimerConstants.RESULT_ADDED;

/**
 * Every app needs a main activity......
 *
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class MainActivity extends AppCompatActivity implements
        EventSelectFragment.OnEventSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DbHelper dbHelper;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());

        if (event == null) {
            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            long eventId = sharedPreferences.getLong(KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
            event = dbHelper.findEvent(eventId);
        }

        Log.d(TAG, String.format("EventId: %s", event));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState");

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ACTIVE_EVENT, event == null ? NO_ACTIVE_EVENT : event.getId());
        editor.apply();

        savedInstanceState.putParcelable(KEY_ACTIVE_EVENT, event);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");

        super.onResume();

        EventSelectFragment esf = (EventSelectFragment) getSupportFragmentManager().
                findFragmentById(R.id.event_select);
        if (esf == null) {
            esf = EventSelectFragment.newInstance(event);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.event_select, esf);
            ft.commit();
        }
        esf.updateSelection(event);

        updateButtonState();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        event = savedInstanceState.getParcelable(KEY_ACTIVE_EVENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Log.d(TAG, "launching settings");
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.clear_data:
                Log.d(TAG, "clearing data");
                String message;
                if (dbHelper.clear_data()) {
                    message = "Data cleared";
                    event = null;
                } else {
                    message = "Could not clear data";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                EventSelectFragment esf = (EventSelectFragment) getSupportFragmentManager().
                        findFragmentById(R.id.event_select);
                esf.refreshData();
                updateButtonState();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, String.format("RequestCode: %d ResultCode: %d Intent: %s",
                requestCode, resultCode, data == null ? data : data.toString()));
        Log.d(TAG, String.format("Event: %s", event == null ? event : event.toString()));
        switch (requestCode) {
            case ADD_EVENT:
                if (resultCode == RESULT_OK) {
                    event = data.getParcelableExtra(KEY_ACTIVE_EVENT);
                } else if (resultCode == RESULT_ADDED) {
                    event = data.getParcelableExtra(KEY_ACTIVE_EVENT);
                }
                break;
            case BUILD_INTERMEDIATES:
                if (resultCode == RESULT_OK) {
                }
                break;
            case BUILD_STARTLIST:
                if (resultCode == RESULT_OK) {
                }
                break;
        }
        updateButtonState();
        Log.d(TAG, String.format("Event: %s", event == null ? event : event.toString()));
    }

    private void updateButtonState() {
        findViewById(R.id.main_button_intermediate).setEnabled(event != null && event.getId() > 0);
        findViewById(R.id.main_button_startlist).setEnabled(event != null && event.getId() > 0);
        findViewById(R.id.main_button_timing).setEnabled(
                event != null && event.getId() > 0
                && DbUtils.getTimingpointCountForEvent(event.getId(), dbHelper) > 0
                && DbUtils.getAthleteCountForEvent(event.getId(), dbHelper) > 0);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, event);
        int request_code = -1;

        switch (view.getId()) {
            case R.id.main_button_event:
                intent.setClass(MainActivity.this, EventActivity.class);
                request_code = ADD_EVENT;
                break;
            case R.id.main_button_intermediate:
                intent.setClass(MainActivity.this, TimingpointActivity.class);
                request_code = BUILD_INTERMEDIATES;
                break;
            case R.id.main_button_startlist:
                intent.setClass(MainActivity.this, StartlistActivity.class);
                request_code = BUILD_STARTLIST;
                break;
            case R.id.main_button_timing:
                intent.setClass(MainActivity.this, TimingActivity.class);
                break;
        }

        startActivityForResult(intent, request_code);
    }

    @Override
    public void onEventSelected(long eventId) {
        Log.d(TAG, String.format("onEventSelected.eventId: %d", eventId));
        this.event = dbHelper.findEvent(eventId);

        updateButtonState();
    }
}