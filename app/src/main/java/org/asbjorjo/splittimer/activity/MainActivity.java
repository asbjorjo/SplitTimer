package org.asbjorjo.splittimer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());
    }

/*
    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_ACTIVE_EVENT, eventId);
        editor.apply();

        super.onDestroy();
    }
*/

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onSaveInstanceState");
        savedInstanceState.putLong(KEY_ACTIVE_EVENT, eventId);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        eventId = savedInstanceState.getLong(KEY_ACTIVE_EVENT, -1);

        if (eventId > 0) {
            findViewById(R.id.main_button_startlist).setEnabled(true);
            findViewById(R.id.main_button_intermediate).setEnabled(true);
        }

        updateTimingButtonState();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "launching settings");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, String.format("RequestCode: %d ResultCode: %d Intent: %s",
                requestCode, resultCode, data == null ? data : data.toString()));
        Log.d(TAG, String.format("EventId: %d", eventId));
        switch (requestCode) {
            case SplitTimerConstants.ADD_EVENT:
                if (resultCode == RESULT_OK) {
                    eventId = data.getLongExtra(KEY_ACTIVE_EVENT, -1);
                    findViewById(R.id.main_button_startlist).setEnabled(true);
                    findViewById(R.id.main_button_intermediate).setEnabled(true);
                } else {
                    eventId = -1;
                    findViewById(R.id.main_button_startlist).setEnabled(false);
                    findViewById(R.id.main_button_intermediate).setEnabled(false);
                }
                updateTimingButtonState();
                break;
            case SplitTimerConstants.BUILD_INTERMEDIATES:
                if (resultCode == RESULT_OK) {
                    updateTimingButtonState();
                }
                break;
            case SplitTimerConstants.BUILD_STARTLIST:
                if (resultCode == RESULT_OK) {
                    updateTimingButtonState();
                }
                break;
        }
        Log.d(TAG, String.format("EventId: %d", eventId));
    }

    private void updateTimingButtonState() {
        findViewById(R.id.main_button_timing).setEnabled(
                eventId > 0
                && DbUtils.getTimingpointCountForEvent(eventId, dbHelper) > 0
                && DbUtils.getAthleteCountForEvent(eventId, dbHelper) > 0);
    }

    public void onClick(View view) {
        Intent intent = new Intent();
        int request_code = -1;

        intent.putExtra(KEY_ACTIVE_EVENT, eventId);

        switch (view.getId()) {
            case R.id.main_button_event:
                intent.setClass(MainActivity.this, EventActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                request_code = SplitTimerConstants.ADD_EVENT;
                break;
            case R.id.main_button_intermediate:
                intent.setClass(MainActivity.this, TimingpointActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                request_code = SplitTimerConstants.BUILD_INTERMEDIATES;
                break;
            case R.id.main_button_startlist:
                intent.setClass(MainActivity.this, StartlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                request_code = SplitTimerConstants.BUILD_STARTLIST;
                break;
            case R.id.main_button_timing:
                intent.setClass(MainActivity.this, TimingActivity.class);
                break;
        }

        startActivityForResult(intent, request_code);
    }
}