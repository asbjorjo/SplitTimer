package org.asbjorjo.splittimer.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance(getApplicationContext());

        updateList();
    }

    private void updateList() {
        String[] from = {
                Contract.Event.KEY_NAME,
                Contract.Event.KEY_DATE
        };
        int[] to = {
                R.id.list_event_name,
                R.id.list_event_date
        };

        Cursor eventCursor = DbUtils.getEvents(dbHelper);
        ListView eventList = (ListView) findViewById(R.id.event_list);
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) eventList.getAdapter();

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this, R.layout.list_event_item, eventCursor,
                    from, to, 0);
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (columnIndex == cursor.getColumnIndex(Contract.Event.KEY_DATE)) {
                        long date = cursor.getLong(columnIndex);
                        TextView textView = (TextView) view;
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(date);

                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

                        textView.setText(df.format(cal.getTime()));
                        return true;
                    }
                    return false;
                }
            });
            eventList.setAdapter(adapter);
            eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    long eventId = parent.getItemIdAtPosition(position);
                    updateActiveEvent(eventId);
                }
            });
        } else {
            Cursor oldCursor = adapter.swapCursor(eventCursor);
            oldCursor.close();
        }
    }

    private void updateActiveEvent(long eventId) {
        Intent result = new Intent();
        result.putExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, eventId);

        setResult(RESULT_OK, result);
    }

    public void addEvent(View view) {
        EditText textView = (EditText) findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) findViewById(R.id.event_input_date);

        Calendar date = Calendar.getInstance();
        date.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

        String eventName = textView.getText().toString();

        String message;

        if (eventName.trim().equals("")) {
            message = "Name missing";
            setResult(RESULT_CANCELED);
        } else {
            ContentValues values = new ContentValues();
            values.put(Contract.Event.KEY_NAME, eventName);
            values.put(Contract.Event.KEY_DATE, date.getTimeInMillis());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            long eventId = database.insert(Contract.Event.TABLE_NAME, null, values);

            textView.setText(null);
            updateActiveEvent(eventId);
            updateList();

            message = String.format("Added %s", eventName);
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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