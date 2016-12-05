package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJohansen2 on 11/23/2016.
 */
public class StartlistActivity extends AppCompatActivity {
    private static final String TAG = "StartlistActivity";
    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        SplitTimerApplication application = (SplitTimerApplication) getApplication();

        eventId = application.getActiveEvent();
        buildList();
    }

    private void buildList() {
        String[] from = {
                Contract.Athlete.KEY_NAME,
                Contract.Athlete.KEY_NUMBER,
                Contract.Startlist.KEY_STARTTIME
        };
        int[] to = {
                R.id.startlist_item_name,
                R.id.startlist_item_number,
                R.id.startlist_item_starttime
        };

        Cursor cursor = DbUtils.getAthletesForEvent(eventId, dbHelper);

        ListAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_startlist_item,
                cursor, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.list_startlist);
        listView.setAdapter(adapter);
    }

    private void updateList() {
        Cursor cursor = DbUtils.getAthletesForEvent(eventId, dbHelper);
        ListView listView = (ListView) findViewById(R.id.list_startlist);
        CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
        Cursor oldCursor = adapter.swapCursor(cursor);
        oldCursor.close();
    }

    public void addAthlete(View view) {
        int number = -1;
        long startTime = -1;
        String message;
        List<String> error = new ArrayList<>();

        EditText nameView = (EditText) findViewById(R.id.startlist_input_name);
        EditText numberView = (EditText) findViewById(R.id.startlist_input_number);
        EditText startView = (EditText) findViewById(R.id.startlist_input_starttime);

        String name = nameView.getText().toString();
        if (name.trim().equals("")) {
            error.add("name");
        }
        try {
            number = Integer.parseInt(numberView.getText().toString());
        } catch (NumberFormatException e) {
            error.add("number");
        }
        try {
            startTime = Long.parseLong(startView.getText().toString());
        } catch (NumberFormatException e) {
            error.add("start time");
        }

        if (error.size() > 0) {
            Log.d(TAG, "Missing some fields");
            StringBuilder stringBuilder = new StringBuilder("Enter ");
            boolean first = true;
            for (String s:error) {
                if (first) first = false;
                else stringBuilder.append(", ");
                stringBuilder.append(s);
            }
            message = stringBuilder.toString();

            setResult(RESULT_CANCELED);
        } else {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues athleteValues = new ContentValues();
            ContentValues eventValues = new ContentValues();
            athleteValues.put(Contract.Athlete.KEY_NAME, name);
            athleteValues.put(Contract.Athlete.KEY_NUMBER, number);
            eventValues.put(Contract.Startlist.KEY_EVENT, eventId);
            eventValues.put(Contract.Startlist.KEY_STARTTIME, startTime);

            database.beginTransaction();
            try {
                long athleteId = database.insert(Contract.Athlete.TABLE_NAME, null, athleteValues);
                eventValues.put(Contract.Startlist.KEY_ATHLETE, athleteId);
                database.insert(Contract.Startlist.TABLE_NAME, null, eventValues);
                database.setTransactionSuccessful();
                message = String.format("Added %s", name);

                nameView.setText(null);
                numberView.setText(null);
                startView.setText(null);
                nameView.requestFocus();

                updateList();

                setResult(RESULT_OK);
            } catch (SQLException e) {
                message = String.format("Error adding %s", name);

                setResult(RESULT_CANCELED);
            } finally {
                database.endTransaction();
            }
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}