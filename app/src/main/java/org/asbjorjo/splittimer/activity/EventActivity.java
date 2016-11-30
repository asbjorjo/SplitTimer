package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.data.Event;
import org.asbjorjo.splittimer.db.EventDataSource;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class EventActivity extends AppCompatActivity {
    private SplitTimerApplication application;
    private EventDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        dataSource = new EventDataSource(this.getApplicationContext());
    }

    public void addEvent(View view) {
        EditText text = (EditText) findViewById(R.id.event_input_name);
        Event event = new Event(text.getText().toString());

        dataSource.open();
        event = dataSource.saveEvent(event);
        dataSource.close();

        application.setEvent(event);

        text.setText(null);
        setResult(RESULT_OK);
    }
}