package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import java.util.Calendar;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class EventActivity extends AppCompatActivity {
    private static final String TAG = "EventActivity";
    private SplitTimerApplication application;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (SplitTimerApplication) getApplication();
        dbHelper = DbHelper.getInstance(getApplicationContext());

        buildList();
    }

    private void buildList() {
        Cursor eventCursor = DbUtils.getEvents(dbHelper);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.list_event_item, eventCursor,
                new String[]{Contract.Event.KEY_NAME}, new int[]{R.id.list_event_name}, 0);
        ListView eventList = (ListView) findViewById(R.id.event_list);
        eventList.setAdapter(listAdapter);
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long eventId = parent.getItemIdAtPosition(position);
                updateActiveEvent(eventId);
            }
        });
    }

    private void updateList() {
        Cursor eventCursor = DbUtils.getEvents(dbHelper);
        ListView eventList = (ListView) findViewById(R.id.event_list);
        CursorAdapter adapter = (CursorAdapter) eventList.getAdapter();
        Cursor oldCursor = adapter.swapCursor(eventCursor);
        oldCursor.close();
    }

    private void updateActiveEvent(long eventId) {
        Intent result = new Intent();
        result.putExtra(SplitTimerConstants.ACTIVE_EVENT, eventId);

        setResult(RESULT_OK, result);
    }

    public void addEvent(View view) {
        EditText textView = (EditText) findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) findViewById(R.id.event_input_date);

        Calendar date = Calendar.getInstance();
        date.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

        String eventName = textView.getText().toString();

        String message;

        if (eventName == null || eventName.trim().equals("")) {
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
}