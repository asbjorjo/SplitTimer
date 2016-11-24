package org.asbjorjo.splittimer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;

import de.codecrafters.tableview.TableView;

/**
 * Created by AJohansen2 on 11/23/2016.
 */
public class StartlistActivity extends AppCompatActivity {
    private SplitTimerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        if (application.getAthleteList() == null) {
            application.setAthleteList(new ArrayList<Athlete>());
        }

        TableView table = (TableView) findViewById(R.id.startlist_table);
        table.setDataAdapter(new AthleteTableDataAdapter(this, application.getAthleteList()));
        table.setColumnCount(3);

    }

    public void addAthlete(View view) {
        EditText nameView = (EditText) findViewById(R.id.startlist_input_name);
        EditText numberView = (EditText) findViewById(R.id.startlist_input_number);
        EditText startView = (EditText) findViewById(R.id.startlist_input_starttime);

        String name = nameView.getText().toString();
        int number = Integer.parseInt(numberView.getText().toString());
        long startTime = Long.parseLong(startView.getText().toString());

        application.getAthleteList().add(new Athlete(name, number, startTime));
        Collections.sort(application.getAthleteList(), new Athlete.StartComparator());

        TableView table = (TableView) findViewById(R.id.startlist_table);
        table.getDataAdapter().notifyDataSetChanged();

        nameView.setText(null);
        numberView.setText(null);
        startView.setText(null);
        
        setResult(RESULT_OK);
    }
}
