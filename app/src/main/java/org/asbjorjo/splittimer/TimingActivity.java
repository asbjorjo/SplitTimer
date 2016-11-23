package org.asbjorjo.splittimer;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.providers.TableDataRowBackgroundProvider;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class TimingActivity extends AppCompatActivity {
    SplitTimerApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();

        if (application.getAthleteList() == null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            initializeTable();
            initializeDropdown();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SplitTimerApplication application = (SplitTimerApplication) getApplication();
    }

    /**
     * Setup table of Athletes.
     */
    private void initializeTable() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
        List<Athlete> athletes = application.getAthleteList();

        if (athletes.get(0).intermediates == null) {
            for (Athlete athlete:athletes
                 ) {
                athlete.intermediates = new long[1];
            }
        }

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
        table.addDataLongClickListener(new AthleteLongClickListener());
        table.setDataRowBackgroundProvider(new AthleteRowColorProvider());
    }

    /**
     * Initialize the Spinner and associated Buttons for intermediate times.
     */
    private void initializeDropdown() {
        List<Athlete> athletes = application.getAthleteList();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Athlete[] athletesList = new Athlete[athletes.size()];
        athletes.toArray(athletesList);
        Arrays.sort(athletesList, new Athlete.NumberComparator());
        ArrayAdapter<Athlete> athleteAdapter = new ArrayAdapter<Athlete>(this, R.layout.support_simple_spinner_dropdown_item, athletesList);
        spinner.setAdapter(athleteAdapter);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_timing);

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
                    application.setReference(selected);
                    SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
                    table.sort(v.getId() - 1337 + 3, true);
                }
            });

            layout.addView(button, params);
        }
    }

    private void sortByReference() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
        Athlete reference = application.getReference();

        int i = 0;
        while (i < reference.intermediates.length && reference.intermediates[i] > 0) {
            i++;
        }

        table.sort(2 + i, true);
    }

    /**
     * Update view as user clicks one rows in table of Athletes.
     */
    private class AthleteClickListener implements TableDataClickListener<Athlete> {
        @Override
        public void onDataClicked(int rowIndex, Athlete athlete) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
            spinner.setSelection(adapter.getPosition(athlete));
        }
    }

    private class AthleteLongClickListener implements TableDataLongClickListener<Athlete> {
        @Override
        public boolean onDataLongClicked(int rowIndex, Athlete athlete) {
            application.setReference(athlete);
            sortByReference();
            return true;
        }
    }

    private class AthleteRowColorProvider implements TableDataRowBackgroundProvider<Athlete> {
        @Override
        public Drawable getRowBackground(final int rowIndex, final Athlete athlete) {
            int rowColor = getResources().getColor(R.color.white);

            if (athlete == application.getReference()) {
                rowColor = getResources().getColor(R.color.gray);
            }

            return new ColorDrawable(rowColor);
        }
    }
}
