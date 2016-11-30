package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.asbjorjo.splittimer.AthleteTableDataAdapter;
import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.data.Athlete;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.ArrayList;
import java.util.Collections;

import de.codecrafters.tableview.TableView;

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

        application = (SplitTimerApplication) getApplication();
        if (application.getEvent().getAthletes() == null) {
            application.getEvent().setAthletes(new ArrayList<Athlete>());
        }

        dbHelper = new DbHelper(getApplicationContext());

        TableView table = (TableView) findViewById(R.id.startlist_table);
        table.setDataAdapter(new AthleteTableDataAdapter(this, application.getEvent().getAthletes()));
        table.setColumnCount(3);

    }

    public void addAthlete(View view) {
        EditText nameView = (EditText) findViewById(R.id.startlist_input_name);
        EditText numberView = (EditText) findViewById(R.id.startlist_input_number);
        EditText startView = (EditText) findViewById(R.id.startlist_input_starttime);

        String name = nameView.getText().toString();
        int number = Integer.parseInt(numberView.getText().toString());
        long startTime = Long.parseLong(startView.getText().toString());

        application.getEvent().getAthletes().add(new Athlete(name, number, startTime));
        Collections.sort(application.getEvent().getAthletes(), new Athlete.StartComparator());

        TableView table = (TableView) findViewById(R.id.startlist_table);
        table.getDataAdapter().notifyDataSetChanged();

        nameView.setText(null);
        numberView.setText(null);
        startView.setText(null);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.Athlete.KEY_NAME, name);
        values.put(Contract.Athlete.KEY_NUMBER, number);
        long id = database.insert(Contract.Athlete.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(Contract.EventAthlete.KEY_ATHLETE, id);
        values.put(Contract.EventAthlete.KEY_EVENT, application.getEvent().getId());
        values.put(Contract.EventAthlete.KEY_STARTTIME, startTime);
        database.insert(Contract.EventAthlete.TABLE_NAME, null, values);
        database.close();

        setResult(RESULT_OK);
    }
}
