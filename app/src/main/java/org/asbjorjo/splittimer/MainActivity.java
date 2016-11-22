package org.asbjorjo.splittimer;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;

public class MainActivity extends AppCompatActivity {
    DataFragment data;
    List<Athlete> athletes;
    Athlete reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getFragmentManager();
        data = (DataFragment) fm.findFragmentByTag("data");

        if (data == null) {
            data = new DataFragment();
            fm.beginTransaction().add(data, "data").commit();
            createAthletes();
            data.setAthletes(athletes);
        }
        athletes = data.getAthletes();
        reference = data.getReference();

        initializeTable();
        initializeDropdown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        data.setAthletes(athletes);
        data.setReference(reference);
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

    private void initializeTable() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
        table.setDataAdapter(new AthleteTableDataAdapter(this, athletes));
        table.setColumnCount(3 + athletes.get(0).intermediates.length);
        table.setColumnComparator(0, new Athlete.NameComparator());
        table.setColumnComparator(1, new Athlete.NumberComparator());
        table.setColumnComparator(2, new Athlete.StartComparator());

        for (int i = 0; i < athletes.get(0).intermediates.length; i++) {
            int column = 3+i;
            table.setColumnComparator(column, new AthleteIntermediateComparator(i));
        }

        table.addDataClickListener(new AthleteClickListener());
    }

    private void initializeDropdown() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Athlete[] athletesList = new Athlete[athletes.size()];
        ArrayAdapter<Athlete> athleteAdapter = new ArrayAdapter<Athlete>(this, R.layout.support_simple_spinner_dropdown_item, athletes.toArray(athletesList));
        spinner.setAdapter(athleteAdapter);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_main);

        for (int i = 0; i < athletes.get(0).intermediates.length; i++) {
            Button button = new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setId(i+1337);
            button.setText("INT " + (i+1));
            if (i != 0) {
                params.addRule(RelativeLayout.RIGHT_OF, button.getId()-1);
            } else {
                params.addRule(RelativeLayout.RIGHT_OF, R.id.spinner);
            }

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    Athlete selected = (Athlete) spinner.getSelectedItem();
                    long time = Calendar.getInstance().getTimeInMillis();
                    selected.intermediates[v.getId() - 1337] = time;
                    if (reference == null) {
                        reference = selected;
                    }
                    TableView table = (TableView) findViewById(R.id.main_table);
                    table.getDataAdapter().notifyDataSetInvalidated();
                }
            });

            layout.addView(button, params);
        }
    }

    private class AthleteClickListener implements TableDataClickListener<Athlete> {
        @Override
        public void onDataClicked(int rowIndex, Athlete athlete) {
            reference = athlete;
            TableView table = (TableView) findViewById(R.id.main_table);
            table.getDataAdapter().notifyDataSetChanged();
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            spinner.setSelection(adapter.getPosition(reference));
        }
    }

    private void createAthletes() {
        String[] names = {"Arne", "Per", "Kari", "Kjersti", "Kjell", "Peder", "Rolf"};

        int number = 1;
        long start = 0;

        athletes = new ArrayList<>();

        for (String name : names
                ) {
            athletes.add(new Athlete(name, number, start, 2));

            number++;
            start += 5*60*1000;
        }
    }
}
