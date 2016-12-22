package org.asbjorjo.splittimer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.fragment.EventAddEditFragment;
import org.asbjorjo.splittimer.fragment.EventEditFragment;
import org.asbjorjo.splittimer.fragment.EventListFragment;

import java.util.Calendar;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_IS_EDITING;
import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.RESULT_ADDED;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class EventActivity extends AppCompatActivity implements
        EventListFragment.OnEventSelectedListener, EventEditFragment.OnEventEditActionListener {
    private static final String TAG = EventActivity.class.getSimpleName();
    private DbHelper dbHelper;
    private long eventId;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance(this);

        Intent intent = getIntent();
        eventId = intent.getLongExtra(KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);

        if (eventId > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_ACTIVE_EVENT, eventId);
            setResult(RESULT_OK, resultIntent);
        }

        Fragment eventInput = getSupportFragmentManager().findFragmentById(R.id.event_input);
        if (eventInput == null) {
            if (isEditing) {
                eventInput = EventEditFragment.newInstance(eventId);
            } else {
                eventInput = EventAddEditFragment.newInstance(eventId);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.event_input, eventInput);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_IS_EDITING, isEditing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        Log.d(TAG, String.format("savedInstanceState: %s", savedInstanceState.toString()));
        super.onRestoreInstanceState(savedInstanceState);

        this.isEditing = savedInstanceState.getBoolean(KEY_IS_EDITING, false);
    }

    @Override
    public void onEventSelected(long eventId) {
        Log.d(TAG, "onEventSelected");

        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, eventId);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onEventSaved(Bundle event) {
        Log.d(TAG, "onEventSaved");

        ContentValues values = new ContentValues();
        values.put(Contract.Event.KEY_NAME, event.getString("name"));
        values.put(Contract.Event.KEY_DATE, event.getLong("date"));
        values.put(Contract.Event.KEY_TYPE, event.getString("type"));

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        eventId = database.insert(Contract.Event.TABLE_NAME, null, values);

        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager().
                findFragmentById(R.id.event_list);
        if (eventListFragment != null) eventListFragment.updateList();

        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, eventId);
        setResult(RESULT_ADDED, intent);

        EventAddEditFragment fragment = EventAddEditFragment.newInstance(eventId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.event_input, fragment);
        ft.commit();
        isEditing = false;
    }

    @Override
    public void onEventCancel() {
        Log.d(TAG, "onEventCancel");
        EventAddEditFragment fragment = EventAddEditFragment.newInstance(eventId);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.event_input, fragment);
        ft.commit();
        isEditing = false;
    }

    public void onClick(View view) {
        EventEditFragment eef;
        FragmentTransaction ft;
        switch (view.getId()) {
            case R.id.event_button_add:
                eef = EventEditFragment.newInstance(NO_ACTIVE_EVENT);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.event_input, eef);
                ft.commit();
                isEditing = true;
                break;
            case R.id.event_button_edit:
                eef = EventEditFragment.newInstance(eventId);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.event_input, eef);
                ft.commit();
                isEditing = false;
                break;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "datePicker");
    }
}