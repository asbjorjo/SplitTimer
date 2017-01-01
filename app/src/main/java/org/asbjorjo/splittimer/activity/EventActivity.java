package org.asbjorjo.splittimer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.fragment.EventAddEditFragment;
import org.asbjorjo.splittimer.fragment.EventEditFragment;
import org.asbjorjo.splittimer.fragment.EventListFragment;
import org.asbjorjo.splittimer.model.Event;

import java.util.Calendar;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_IS_EDITING;
import static org.asbjorjo.splittimer.SplitTimerConstants.RESULT_ADDED;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class EventActivity extends AppCompatActivity implements
        EventListFragment.OnEventSelectedListener, EventEditFragment.OnEventEditActionListener {
    private static final String TAG = EventActivity.class.getSimpleName();
    private DbHelper dbHelper;
    private Event event;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(this);

        event = getIntent().getParcelableExtra(KEY_ACTIVE_EVENT);

        Fragment eventInput = getSupportFragmentManager().findFragmentById(R.id.event_input);
        if (eventInput == null) {
            if (isEditing) {
                eventInput = EventEditFragment.newInstance(event);
            } else {
                eventInput = EventAddEditFragment.newInstance(event);
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
        event = dbHelper.findEvent(eventId);
        Log.d(TAG, event.toString());
        intent.putExtra(KEY_ACTIVE_EVENT, event);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onEventSaved(Event event) {
        Log.d(TAG, "onEventSaved");

        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager().
                findFragmentById(R.id.event_list);
        if (eventListFragment != null) eventListFragment.updateList();

        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, event);
        setResult(RESULT_ADDED, intent);

        EventAddEditFragment fragment = EventAddEditFragment.newInstance(event);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.event_input, fragment);
        ft.commit();

        this.event = event;
        isEditing = false;
    }

    @Override
    public void onEventCancel() {
        Log.d(TAG, "onEventCancel");
        EventAddEditFragment fragment = EventAddEditFragment.newInstance(event);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.event_input, fragment);
        ft.commit();
        isEditing = false;

        if (event != null) {
            Intent intent = new Intent();
            intent.putExtra(KEY_ACTIVE_EVENT, event);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
    }

    public void onClick(View view) {
        EventEditFragment eef;
        FragmentTransaction ft;
        switch (view.getId()) {
            case R.id.event_button_add:
                eef = EventEditFragment.newInstance(null);
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.event_input, eef);
                ft.commit();
                isEditing = true;
                break;
            case R.id.event_button_edit:
                eef = EventEditFragment.newInstance(event);
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