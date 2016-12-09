package org.asbjorjo.splittimer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.fragment.EventEditFragment;
import org.asbjorjo.splittimer.fragment.EventListFragment;

import java.util.Calendar;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.RESULT_ADDED;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */
public class EventActivity extends AppCompatActivity implements
        EventListFragment.OnEventSelectedListener, EventEditFragment.OnEventAddedListener {
    private static final String TAG = EventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));
        Log.d(TAG, String.format("savedInstanceState: %s",
                savedInstanceState == null ? savedInstanceState : savedInstanceState.toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        long eventId = intent.getLongExtra(KEY_ACTIVE_EVENT, -1);

        if (eventId > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_ACTIVE_EVENT, eventId);
            setResult(RESULT_OK, resultIntent);
        }
    }

    @Override
    public void onEventSelected(long eventId) {
        Log.d(TAG, "onEventSelected");

        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, eventId);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onEventAdded(long eventId) {
        Log.d(TAG, "oneEventAdded");

        Intent intent = new Intent();
        intent.putExtra(KEY_ACTIVE_EVENT, eventId);
        setResult(RESULT_ADDED, intent);
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