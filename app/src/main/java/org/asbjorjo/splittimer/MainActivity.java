package org.asbjorjo.splittimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
        if (application.getAthleteList() != null && application.getAthleteList().size() > 0 && application.getIntermediates() != null && application.getIntermediates().size() > 0) {
            findViewById(R.id.main_button_timing).setEnabled(true);
        }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SplitTimerApplication application = (SplitTimerApplication) getApplication();
        switch (requestCode) {
            case R.integer.BUILD_STARTLIST:
                if (resultCode == RESULT_OK) {
                    Button button = (Button) findViewById(R.id.main_button_timing);
                    button.setEnabled(application.getIntermediates() != null && application.getIntermediates().size() > 0);
                }
                break;
            case R.integer.BUILD_INTERMEDIATES:
                if (resultCode == RESULT_OK) {
                    Button button = (Button) findViewById(R.id.main_button_timing);
                    button.setEnabled(application.getAthleteList() != null && application.getAthleteList().size() > 0);
                }
                break;
        }
    }

    public void onClick(View view) {
        Intent intent = null;
        int request_code = -1;

        switch (view.getId()) {
            case R.id.main_button_intermediate:
                intent = new Intent(this, IntermediateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                request_code = R.integer.BUILD_INTERMEDIATES;
                break;
            case R.id.main_button_startlist:
                intent = new Intent(this, StartlistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                request_code = R.integer.BUILD_STARTLIST;
                break;
            case R.id.main_button_timing:
                intent = new Intent(this, TimingActivity.class);
                break;
        }
        startActivityForResult(intent, request_code);
    }

    /**
     * Generate a list of Athletes at 5 minute start invervals for testing.
      */
    private static List<Athlete> createAthletes() {
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
