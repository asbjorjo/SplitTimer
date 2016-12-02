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
import org.asbjorjo.splittimer.data.Event;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class EventActivity extends AppCompatActivity {
    private SplitTimerApplication application;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        dbHelper = DbHelper.getInstance(getApplicationContext());

        buildList();
    }

    private void buildList() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor eventCursor = database.query(Contract.Event.TABLE_NAME, Contract.Event.KEYS,
                null, null, null, null, Contract.Event.DEFAULT_SORT_ORDER);
        ListAdapter listAdapter = new SimpleCursorAdapter(this, R.layout.list_event_item, eventCursor,
                new String[]{Contract.Event.KEY_NAME}, new int[]{R.id.list_event_name}, 0);
        ListView eventList = (ListView) findViewById(R.id.event_list);
        eventList.setAdapter(listAdapter);
    }

    public void addEvent(View view) {
        EditText text = (EditText) findViewById(R.id.event_input_name);
        Event event = new Event(text.getText().toString());

        application.setEvent(event);

        ContentValues values = new ContentValues();
        values.put(Contract.Event.KEY_NAME, event.getName());

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(Contract.Event.TABLE_NAME, null, values);
        event.setId(id);

        text.setText(null);
        buildList();
        setResult(RESULT_OK);
    }
}