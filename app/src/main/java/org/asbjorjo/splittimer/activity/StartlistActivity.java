package org.asbjorjo.splittimer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.DbHelper;

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
        setContentView(R.layout.startlist_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}