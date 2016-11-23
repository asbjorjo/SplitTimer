package org.asbjorjo.splittimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SplitTimerApplication application = (SplitTimerApplication) getApplication();
        if (application.getAthleteList() == null) {
            application.setAthleteList(createAthletes());
        } else {
            for (Athlete athlete:application.getAthleteList()
                 ) {
                for (int i = 0; i < athlete.intermediates.length; i++) {
                    athlete.intermediates[i] = 0;
                }
            }
        }

        application.setReference(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TimingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), StartlistActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Generate a list of Athletes at 5 minute start invervals for testing.
      */
    private List<Athlete> createAthletes() {
        String[] names = {"Arne", "Per", "Kari", "Kjersti", "Kjell", "Peder", "Rolf"};

        int number = 1;
        long start = 0;

        List<Athlete> athletes = new ArrayList<>();

        for (String name : names
                ) {
            athletes.add(new Athlete(name, number, start, 2));

            number++;
            start += 5*1000;
        }

        return athletes;
    }
}
