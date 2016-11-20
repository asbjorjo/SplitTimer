package org.asbjorjo.splittimer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

public class MainActivity extends AppCompatActivity {
    ArrayList<Athlete> athletes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        createAthletes();
        initializeTable();
    }

    private void initializeTable() {
        TableView table = (TableView) findViewById(R.id.main_table);
        table.setDataAdapter(new AthleteTableDataAdapter(this, athletes));

    }

    private void createAthletes() {
        Calendar cal = Calendar.getInstance();
        athletes.add(new Athlete("Arne", 1L, cal.getTimeInMillis()));
        cal.add(Calendar.MINUTE, 5);
        athletes.add(new Athlete("Per", 2L, cal.getTimeInMillis()));
        cal.add(Calendar.MINUTE, 5);
        athletes.add(new Athlete("Kari", 3L, cal.getTimeInMillis()));
        cal.add(Calendar.MINUTE, 5);
        athletes.add(new Athlete("Kjersti", 4L, cal.getTimeInMillis()));
        cal.add(Calendar.MINUTE, 5);
        athletes.add(new Athlete("Kjell", 5L, cal.getTimeInMillis()));
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
}
