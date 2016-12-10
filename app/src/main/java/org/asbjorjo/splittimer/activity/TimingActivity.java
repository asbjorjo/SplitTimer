package org.asbjorjo.splittimer.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import org.asbjorjo.splittimer.AthleteTableDataAdapter;
import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
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

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.PREFS_NAME;
import static org.asbjorjo.splittimer.db.Contract.Athlete;
import static org.asbjorjo.splittimer.db.Contract.Result;
import static org.asbjorjo.splittimer.db.Contract.Timingpoint;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class TimingActivity extends AppCompatActivity {
    private static final String TAG = TimingActivity.class.getSimpleName();
    private static final String REFERENCE_ATHLETE = "referenceAthlete";
    private DbHelper dbHelper;
    private long eventId;
    private long referenceAthlete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, String.format("Intent: %s",
                getIntent() == null ? null : getIntent().toString()));
        Log.d(TAG, String.format("savedInstanceState: %s",
                savedInstanceState == null ? savedInstanceState : savedInstanceState.toString()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.timing_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        dbHelper = DbHelper.getInstance(getApplicationContext());
        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);


        if (referenceAthlete <= 0) {
            if (savedInstanceState != null) {
                referenceAthlete = savedInstanceState.getLong(REFERENCE_ATHLETE);
            } else {
                loadReference();
            }
        }

        Log.d(TAG, String.format("referenceAthlete = %d", referenceAthlete));

        if (eventId > 0) {
            initializeDropdown();
            initializeTable();
        } else {
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        sortByReference();
   }

    @Override
    protected void onStop() {
        super.onStop();

        saveReference();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        referenceAthlete = savedInstanceState.getLong(REFERENCE_ATHLETE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putLong(REFERENCE_ATHLETE, referenceAthlete);
        saveReference();
    }

    private void saveReference() {
        Log.d(TAG, String.format("saveReference - referenceAthlete: %d", referenceAthlete));
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(REFERENCE_ATHLETE, referenceAthlete);
        editor.apply();
    }

    private void loadReference() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        referenceAthlete = sharedPreferences.getLong(REFERENCE_ATHLETE, -1);
    }

    /**
     * Setup table of Athletes.
     */
    private void initializeTable() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
        int timingpointCount = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);

        List<TableAthlete> athletes = updateAthleteList();

        table.setDataAdapter(new AthleteTableDataAdapter(this, athletes));
        updateAthleteTimes();
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

    private List<TableAthlete> updateAthleteList() {
        int timingpointCount = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);
        Cursor athleteCursor = DbUtils.getAthletesForEvent(eventId, dbHelper);
        List<TableAthlete> athletes = new ArrayList<>();

        while (athleteCursor.moveToNext()) {
            long[] times = new long[timingpointCount+1];
            long id = athleteCursor.getLong(athleteCursor.getColumnIndex(Athlete._ID));
            String name = athleteCursor.getString(athleteCursor.getColumnIndex(Athlete.KEY_NAME));
            int number = athleteCursor.getInt(athleteCursor.getColumnIndex(Athlete.KEY_NUMBER));

            athletes.add(new TableAthlete(id, name, number, times));
        }

        return athletes;
    }

    /**
     * Initialize the Spinner and associated Buttons for intermediate times.
     */
    private void initializeDropdown() {
        final String[] from = {Athlete.KEY_NAME};
        final int[] to = {R.id.text_dropdown};

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Cursor athleteCursor = DbUtils.getAthletesForEvent(eventId, dbHelper);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.simple_textview, athleteCursor, from, to, 0);

        spinner.setAdapter(adapter);

        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);

        while (timingpointCursor.moveToNext()) {
            String description = timingpointCursor.getString(timingpointCursor.getColumnIndex(Timingpoint.KEY_DESCRIPTION));
            RelativeLayout buttonLayout = (RelativeLayout) findViewById(R.id.timing_buttons);

            Button button = new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setId(1337 + timingpointCursor.getPosition());
            button.setText(description);

            if (timingpointCursor.getPosition() == 0) {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            } else {
                params.addRule(RelativeLayout.RIGHT_OF, button.getId()-1);
            }

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    Spinner spinner = (Spinner) findViewById(R.id.spinner);
                    long selectedId =  spinner.getSelectedItemId();

                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    Cursor cursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);
                    cursor.moveToPosition(v.getId() - 1337);
                    ContentValues values = new ContentValues();
                    values.put(Result.KEY_ATHLETE, selectedId);
                    values.put(Result.KEY_TIMESTAMP, time);
                    values.put(Result.KEY_TIMINGPOINT, cursor.getLong(
                            cursor.getColumnIndex(Timingpoint._ID)
                    ));
                    cursor.close();
                    database.insert(Result.TABLE_NAME, null, values);

                    referenceAthlete = selectedId;

                    updateButtonState();
                    updateAthleteTimes();

                    SortableTableView table = (SortableTableView) findViewById(R.id.main_table);
                    table.sort(v.getId() - 1337 + 3, true);
                }
            });

            buttonLayout.addView(button, params);
        }
        timingpointCursor.close();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateButtonState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateAthleteTimes() {
        int timingpoints = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);
        TableView tableView = (TableView) findViewById(R.id.main_table);
        List<TableAthlete> tableAthletes = tableView.getDataAdapter().getData();

        for (TableAthlete athlete:tableAthletes) {
            long[] times = new long[timingpoints+1];
            times[0] = DbUtils.getStartTime(eventId, athlete.getId(), dbHelper);

            for (int i=1;i<times.length;i++) {
                times[i] = Long.MAX_VALUE;
            }

            if (referenceAthlete > 0) {
                Cursor standings = DbUtils.getStandingForAthlete(eventId, athlete.getId(), referenceAthlete,
                        dbHelper);
                while (standings.moveToNext()) {
                    times[standings.getPosition()+1] = standings.getLong(
                            standings.getColumnIndex("diff"));
                }
                standings.close();
            }

            athlete.setTimes(times);
        }
        tableView.getDataAdapter().notifyDataSetChanged();
    }

    private void sortByReference() {
        SortableTableView table = (SortableTableView) findViewById(R.id.main_table);

        int passingsByAthlete = DbUtils.getPassingsForAthlete(referenceAthlete, eventId, dbHelper);

        table.sort(2 + passingsByAthlete, true);
    }

    /**
     * Update view as user clicks one rows in table of Athletes.
     */
    private class AthleteClickListener implements TableDataClickListener<TableAthlete> {
        @Override
        public void onDataClicked(int rowIndex, TableAthlete athlete) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner);

            int count = spinner.getCount();
            for (int i = 0; i < count; i++) {
                if (athlete.getId() == spinner.getItemIdAtPosition(i)) {
                    spinner.setSelection(i);
                    break;
                }
            }
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

            if (athlete.getId() == referenceAthlete) {
                rowColor = getResources().getColor(R.color.gray);
            }

            return new ColorDrawable(rowColor);
        }
    }

    private void updateButtonState() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        long athleteId = spinner.getSelectedItemId();
        int timingpoints = DbUtils.getTimingpointCountForEvent(eventId, dbHelper);
        int athletePassings = DbUtils.getPassingsForAthlete(athleteId, eventId, dbHelper);

        for (int i = 0; i < timingpoints; i++) {
            Button button = (Button) findViewById(1337 + i);
            if (athletePassings == i) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }
    }
}