package org.asbjorjo.splittimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.asbjorjo.splittimer.data.Event;

import java.util.ArrayList;

/**
 * Created by AJohansen2 on 11/24/2016.
 */
public class EventActivity extends AppCompatActivity {
    private SplitTimerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        if (application.getEventList() == null) {
            application.setEventList(new ArrayList<Event>());
        }
    }

    public void addEvent(View view) {
        EditText text = (EditText) findViewById(R.id.event_input_name);
        Event event = new Event(text.getText().toString());

        application.getEventList().add(event);
        application.setEvent(event);

        setResult(RESULT_OK);
    }
}