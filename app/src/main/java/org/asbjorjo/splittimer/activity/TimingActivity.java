package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import org.asbjorjo.splittimer.AthleteTableDataAdapter;
import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerApplication;
import org.asbjorjo.splittimer.TableAthlete;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;
import de.codecrafters.tableview.providers.TableDataRowBackgroundProvider;

import static org.asbjorjo.splittimer.db.Contract.Athlete;
import static org.asbjorjo.splittimer.db.Contract.EventAthlete;
import static org.asbjorjo.splittimer.db.Contract.Intermediate;
import static org.asbjorjo.splittimer.db.Contract.IntermediateAthlete;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class TimingActivity extends AppCompatActivity {
    private static final String TAG = "TimingActivity";
    private SplitTimerApplication application;
    private DbHelper dbHelper;
    private long referenceAthlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        application = (SplitTimerApplication) getApplication();
        dbHelper = DbHelper.getInstance(getApplicationContext());

        if (application.getActiveEvent() > 0) {
            initializeTable();
            initializeDropdown();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Setup table of Athletes.
     */
    private void initializeTable() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
        long eventId = application.getActiveEvent();
        int timingpointCount = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);

        Cursor athleteCursor = DbUtils.getAthletesForEvent(application.getActiveEvent(), dbHelper);
        List<TableAthlete> athletes = new ArrayList<>();

        while (athleteCursor.moveToNext()) {
            long id = athleteCursor.getLong(athleteCursor.getColumnIndex(Athlete._ID));
            String name = athleteCursor.getString(athleteCursor.getColumnIndex(Athlete.KEY_NAME));
            int number = athleteCursor.getInt(athleteCursor.getColumnIndex(Athlete.KEY_NUMBER));
            long startTime = athleteCursor.getLong(athleteCursor.getColumnIndex(
                    EventAthlete.KEY_STARTTIME));

            long[] times = new long[timingpointCount+1];

            times[0] = startTime;

            if (referenceAthlete > 0) {
                Cursor standings = DbUtils.getStandingForAthlete(eventId, id, referenceAthlete,
                        dbHelper);
                while (standings.moveToNext()) {
                    times[standings.getPosition()+1] = standings.getLong(
                            standings.getColumnIndex("diff"));
                }
            } else {
                for (int i=1;i<times.length;i++) {
                    times[i] = Long.MIN_VALUE;
                }
            }

            athletes.add(new TableAthlete(id, name, number, times));
        }

        table.setDataAdapter(new AthleteTableDataAdapter(this, athletes));
        table.setColumnCount(3 + timingpointCount);
        table.setColumnComparator(0, new TableAthlete.TableAthleteNameComparator());
        table.setColumnComparator(1, new TableAthlete.TableAthleteNumberComparator());
        table.setColumnComparator(2, new TableAthlete.TableAthleteTimeComparator(0));

        for (int i = 1; i <= timingpointCount; i++) {
            int column = 2+i;
            table.setColumnComparator(column, new TableAthlete.TableAthleteTimeComparator(i));
        }

        table.addDataClickListener(new AthleteClickListener());
        table.addDataLongClickListener(new AthleteLongClickListener());
        table.setDataRowBackgroundProvider(new AthleteRowColorProvider());
    }

    /**
     * Initialize the Spinner and associated Buttons for intermediate times.
     */
    private void initializeDropdown() {
        String[] from = new String[]{Athlete.KEY_NAME};
        int[] to = new int[]{R.id.text_dropdown};
        final long eventId = application.getActiveEvent();

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Cursor athleteCursor = DbUtils.getAthletesForEvent(eventId, dbHelper);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.support_simple_spinner_dropdown_item, athleteCursor, from, to, 0);

        spinner.setAdapter(adapter);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content_timing);

        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);

        while (timingpointCursor.moveToNext()) {
            String description = timingpointCursor.getString(timingpointCursor.getColumnIndex(Intermediate.KEY_DESCRIPTION));

            Button button = new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setId(1337 + timingpointCursor.getPosition());
            button.setText(description);

            if (timingpointCursor.getPosition() == 0) {
                params.addRule(RelativeLayout.RIGHT_OF, R.id.spinner);
            } else {
                params.addRule(RelativeLayout.RIGHT_OF, button.getId()-1);
            }

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    Cursor selected = (Cursor) spinner.getSelectedItem();
                    long time = Calendar.getInstance().getTimeInMillis();
                    long selectedId =  selected.getLong(selected.getColumnIndex(Athlete._ID));

                    SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
                    table.sort(v.getId() - 1337 + 3, true);

                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    Cursor cursor = database.query(Intermediate.TABLE_NAME,
                            new String[]{Intermediate._ID},
                            Intermediate.KEY_EVENT + " = ?",
                            new String[]{Long.toString(application.getActiveEvent())},
                            null, null, Intermediate.DEFAULT_SORT_ORDER);
                    cursor.moveToPosition(v.getId() - 1337);
                    ContentValues values = new ContentValues();
                    values.put(IntermediateAthlete.KEY_ATHLETE, referenceAthlete);
                    values.put(IntermediateAthlete.KEY_TIMESTAMP, time);
                    values.put(IntermediateAthlete.KEY_INTERMEDIATE, cursor.getLong(
                            cursor.getColumnIndex(Intermediate._ID)
                    ));
                    database.insert(IntermediateAthlete.TABLE_NAME, null, values);

                    updateButtonState();
                    referenceAthlete = selectedId;
                    updateAthleteTimes();
                }
            });

            layout.addView(button, params);
        }
        updateButtonState();
    }

    private void updateAthleteTimes() {
        Log.d(TAG, "updateAthleteTimes");

        long eventId = application.getActiveEvent();
        int timingpoints = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);
        TableView tableView = (TableView) findViewById(R.id.main_table);
        List<TableAthlete> tableAthletes = tableView.getDataAdapter().getData();

        for (TableAthlete athlete:tableAthletes) {
            long[] times = new long[timingpoints];
            times[0] = DbUtils.getStartTime(eventId, athlete.getId(), dbHelper);

            if (referenceAthlete > 0) {
                    Cursor standings = DbUtils.getStandingForAthlete(eventId, athlete.getId(), referenceAthlete,
                            dbHelper);
                    while (standings.moveToNext()) {
                        athlete.getTimes()[standings.getPosition()+1] = standings.getLong(
                                standings.getColumnIndex("diff"));
                    }
            }
        }
        tableView.getDataAdapter().notifyDataSetChanged();
    }

    private void sortByReference() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);

        int passingsByAthlete = DbUtils.getPassingsForAthlete(referenceAthlete,
                application.getActiveEvent(), dbHelper);

        table.sort(2 + passingsByAthlete, true);
    }

    /**
     * Update view as user clicks one rows in table of Athletes.
     */
    private class AthleteClickListener implements TableDataClickListener<TableAthlete> {
        @Override
        public void onDataClicked(int rowIndex, TableAthlete athlete) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);

            Cursor cursor = ((CursorAdapter)spinner.getAdapter()).getCursor();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                if (cursor.getLong(cursor.getColumnIndex(Athlete._ID)) == athlete.getId()) {
                    spinner.setSelection(cursor.getPosition());
                    break;
                }
            }

            updateButtonState();
        }
    }

    private class AthleteLongClickListener implements TableDataLongClickListener<TableAthlete> {
        @Override
        public boolean onDataLongClicked(int rowIndex, TableAthlete athlete) {
            referenceAthlete = athlete.getId();
            updateAthleteTimes();
            sortByReference();
            return true;
        }
    }

    private class AthleteRowColorProvider implements TableDataRowBackgroundProvider<TableAthlete> {
        @Override
        public Drawable getRowBackground(final int rowIndex, final TableAthlete athlete) {
            int rowColor = getResources().getColor(R.color.white);

            Log.d(TAG, "Coloring row: " + rowIndex + " athlete: " + athlete.getId() +
                    " reference: " + referenceAthlete);
            if (athlete.getId() == referenceAthlete) {
                rowColor = getResources().getColor(R.color.gray);
            }

            return new ColorDrawable(rowColor);
        }
    }

    private void updateButtonState() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Cursor athlete = (Cursor) spinner.getSelectedItem();
        long athleteId = athlete.getLong(athlete.getColumnIndex(Athlete._ID));
        int timingpoints = DbUtils.getTimingpointCountForEvent(application.getActiveEvent(),
                dbHelper);
        int athletePassings = DbUtils.getPassingsForAthlete(athleteId, application.getActiveEvent(),
                dbHelper);

        for (int i = 0; i < timingpoints; i++) {
            Button button = (Button) findViewById(1337 + i);
            if (athletePassings <= i) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }
    }
}