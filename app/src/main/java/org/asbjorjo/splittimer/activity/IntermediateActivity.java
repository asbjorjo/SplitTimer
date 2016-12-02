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

import java.util.ArrayList;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class IntermediateActivity extends AppCompatActivity {
    private SplitTimerApplication application;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        dbHelper = DbHelper.getInstance(getApplicationContext());

        if (application.getEvent() == null) {

        } else if (application.getEvent().getIntermediates() == null) {
            application.getEvent().setIntermediates(new ArrayList<String>());
        }

        buildList(application.getEvent().getId());
    }

    private void buildList(long eventId) {
        String[] from = new String[]{
                Contract.Intermediate.KEY_DESCRIPTION,
                Contract.Intermediate.KEY_POSITION
        };
        int[] to = new int[]{
                R.id.list_timingpoint_description,
                R.id.list_timingpoint_position
        };

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor timingpointCursor = database.query(Contract.Intermediate.TABLE_NAME,
                Contract.Intermediate.KEYS,
                Contract.Intermediate.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, Contract.Intermediate.DEFAULT_SORT_ORDER);
        CursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_timingpoint_item,
                timingpointCursor, from, to, 0);
        ListView listView = (ListView) findViewById(R.id.intermediate_list);
        listView.setAdapter(adapter);
    }

    public void addIntermediate(View view) {
        EditText text = (EditText) findViewById(R.id.intermediate_input_description);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Intermediate.KEY_EVENT, application.getEvent().getId());
        values.put(Contract.Intermediate.KEY_DESCRIPTION, text.getText().toString());
        values.put(Contract.Intermediate.KEY_POSITION, application.getEvent().getIntermediates().size());
        database.insert(Contract.Intermediate.TABLE_NAME, null, values);

        application.getEvent().getIntermediates().add(text.getText().toString());

        buildList(application.getEvent().getId());

        text.setText(null);
        setResult(RESULT_OK);
    }
}