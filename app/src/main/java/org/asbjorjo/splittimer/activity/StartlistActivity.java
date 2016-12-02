package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.data.Athlete;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.ArrayList;

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

        if (application.getEvent().getAthletes() == null) {
            application.getEvent().setAthletes(new ArrayList<Athlete>());
        }

        buildList(application.getEvent().getId());
    }

    private void buildList(long eventId) {
        String[] from = new String[]{
                Contract.Athlete.KEY_NAME,
                Contract.Athlete.KEY_NUMBER,
                Contract.EventAthlete.KEY_STARTTIME
        };
        int[] to = new int[]{
                R.id.startlist_item_name,
                R.id.startlist_item_number,
                R.id.startlist_item_starttime
        };

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.EventAthlete.TABLE_NAME + " JOIN "
                + Contract.Athlete.TABLE_NAME + " ON " + Contract.Athlete._ID + " = "
                + Contract.EventAthlete.KEY_ATHLETE);
        queryBuilder.appendWhere(Contract.EventAthlete.KEY_EVENT + " = ?");
        String query = queryBuilder.buildQuery(null, null, null, null,
                Contract.EventAthlete.DEFAULT_SORT_ORDER, null);
        Log.d(getClass().getSimpleName(), query);
        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(eventId)});

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
        eventValues.put(Contract.EventAthlete.KEY_EVENT, application.getEvent().getId());
        eventValues.put(Contract.EventAthlete.KEY_STARTTIME, startTime);

        database.beginTransaction();
        try {
            long athleteId = database.insert(Contract.Athlete.TABLE_NAME, null, athleteValues);
            eventValues.put(Contract.EventAthlete.KEY_ATHLETE, athleteId);
            database.insert(Contract.EventAthlete.TABLE_NAME, null, eventValues);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        nameView.setText(null);
        numberView.setText(null);
        startView.setText(null);

        buildList(application.getEvent().getId());

        setResult(RESULT_OK);
    }
}
