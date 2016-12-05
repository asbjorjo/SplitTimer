package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class IntermediateActivity extends AppCompatActivity {
    private SplitTimerApplication application;
    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (SplitTimerApplication) getApplication();
        dbHelper = DbHelper.getInstance(getApplicationContext());
        eventId = application.getActiveEvent();

        buildList();
    }

    private void buildList() {
        String[] from = new String[]{
                Contract.Timingpoint.KEY_DESCRIPTION,
                Contract.Timingpoint.KEY_POSITION
        };
        int[] to = new int[]{
                R.id.list_timingpoint_description,
                R.id.list_timingpoint_position
        };

        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);
        CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_timingpoint_item,
                timingpointCursor, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        listView.setAdapter(adapter);
    }

    public void addIntermediate(View view) {
        EditText text = (EditText) findViewById(R.id.intermediate_input_description);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Timingpoint.KEY_EVENT, eventId);
        values.put(Contract.Timingpoint.KEY_DESCRIPTION, text.getText().toString());
        values.put(Contract.Timingpoint.KEY_POSITION, DbUtils.getTimingpointCountForEvent(
                eventId, dbHelper
        ));
        database.insert(Contract.Timingpoint.TABLE_NAME, null, values);

        text.setText(null);
        updateList();
        setResult(RESULT_OK);
    }

    private void updateList() {
        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);
        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        CursorAdapter adapter = (CursorAdapter) listView.getAdapter();
        Cursor oldCursor = adapter.swapCursor(timingpointCursor);
        oldCursor.close();
    }
}