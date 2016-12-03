package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

/**
 * Created by AJohansen2 on 11/23/2016.
 */
public class StartlistActivity extends AppCompatActivity {
    private SplitTimerApplication application;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = DbHelper.getInstance(getApplicationContext());
        application = (SplitTimerApplication) getApplication();

        buildList(application.getActiveEvent());
    }

    private void buildList(long eventId) {
        String[] from = new String[]{
                Contract.Athlete.KEY_NAME,
                Contract.Athlete.KEY_NUMBER,
                Contract.Startlist.KEY_STARTTIME
        };
        int[] to = new int[]{
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


    public void addAthlete(View view) {
        EditText nameView = (EditText) findViewById(R.id.startlist_input_name);
        EditText numberView = (EditText) findViewById(R.id.startlist_input_number);
        EditText startView = (EditText) findViewById(R.id.startlist_input_starttime);

        String name = nameView.getText().toString();
        int number = Integer.parseInt(numberView.getText().toString());
        long startTime = Long.parseLong(startView.getText().toString());

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues athleteValues = new ContentValues();
        ContentValues eventValues = new ContentValues();
        athleteValues.put(Contract.Athlete.KEY_NAME, name);
        athleteValues.put(Contract.Athlete.KEY_NUMBER, number);
        eventValues.put(Contract.Startlist.KEY_EVENT, application.getActiveEvent());
        eventValues.put(Contract.Startlist.KEY_STARTTIME, startTime);

        database.beginTransaction();
        try {
            long athleteId = database.insert(Contract.Athlete.TABLE_NAME, null, athleteValues);
            eventValues.put(Contract.Startlist.KEY_ATHLETE, athleteId);
            database.insert(Contract.Startlist.TABLE_NAME, null, eventValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        nameView.setText(null);
        numberView.setText(null);
        startView.setText(null);
        nameView.requestFocus();

        buildList(application.getActiveEvent());

        setResult(RESULT_OK);
    }
}
