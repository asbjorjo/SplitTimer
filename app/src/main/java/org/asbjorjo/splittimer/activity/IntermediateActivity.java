package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class IntermediateActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timingpoint_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        dbHelper = DbHelper.getInstance(getApplicationContext());
        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, -1);

        updateList();
    }

    private void updateList() {
        final String[] from = {
                Contract.Timingpoint.KEY_DESCRIPTION,
                Contract.Timingpoint.KEY_POSITION
        };
        final int[] to = {
                R.id.list_timingpoint_description,
                R.id.list_timingpoint_position
        };

        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);
        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        CursorAdapter adapter = (CursorAdapter) listView.getAdapter();

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(this, R.layout.timingpoint_list_item,
                    timingpointCursor, from, to, 0);
            listView.setAdapter(adapter);
        } else {
            Cursor oldCursor = adapter.swapCursor(timingpointCursor);
            oldCursor.close();
        }
    }

    public void addIntermediate(View view) {
        String message;

        EditText text = (EditText) findViewById(R.id.intermediate_input_description);
        String description = text.getText().toString();

        if (description.trim().equals("")) {
            message = "Enter description";

            setResult(RESULT_CANCELED);
        } else {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Contract.Timingpoint.KEY_EVENT, eventId);
            values.put(Contract.Timingpoint.KEY_DESCRIPTION, description);
            values.put(Contract.Timingpoint.KEY_POSITION, DbUtils.getTimingpointCountForEvent(
                    eventId, dbHelper
            ));
            database.insert(Contract.Timingpoint.TABLE_NAME, null, values);

            message = String.format("Added %s", description);

            text.setText(null);
            updateList();
            setResult(RESULT_OK);
        }

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}