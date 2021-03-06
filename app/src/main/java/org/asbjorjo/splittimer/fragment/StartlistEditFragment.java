package org.asbjorjo.splittimer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants.OFFSET_UNIT;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.model.Event;

import java.util.ArrayList;
import java.util.List;

import static org.asbjorjo.splittimer.SplitTimerConstants.EVENT_TYPE.MASS_START;
import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartlistEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = StartlistEditFragment.class.getSimpleName();

    private DbHelper dbHelper;
    private Event event;
    private OnStartlistEntryAddedListener mListener;

    public static StartlistEditFragment newInstance(Event event) {
        StartlistEditFragment sef = new StartlistEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_ACTIVE_EVENT, event);
        sef.setArguments(args);

        return sef;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStartlistEntryAddedListener) {
            mListener = (OnStartlistEntryAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStartlistEntryAddedListener");
        }

        dbHelper = DbHelper.getInstance(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable(KEY_ACTIVE_EVENT);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.startlist_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner = (Spinner) view.findViewById(R.id.startlist_input_starttime_unit);
        SpinnerAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.simple_textview,
                OFFSET_UNIT.values());
        spinner.setAdapter(adapter);

        //TODO Implement properly and for all event types.
        switch (event.getType()) {
            case MASS_START:
                view.findViewById(R.id.startlist_input_offset).setVisibility(View.GONE);
                break;
        }

        view.findViewById(R.id.startlist_input_button).setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        dbHelper = null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.startlist_input_button) {
            Log.d(TAG, "Adding athlete");
            addAthlete();
        }
    }

    public void addAthlete() {
        int number = -1;
        long startTime = -1;
        String message;
        OFFSET_UNIT unit = null;
        List<String> error = new ArrayList<>();

        EditText nameView = (EditText) getView().findViewById(R.id.startlist_input_name);
        EditText numberView = (EditText) getView().findViewById(R.id.startlist_input_number);
        EditText startView = (EditText) getView().findViewById(R.id.startlist_input_starttime);

        String name = nameView.getText().toString();
        if (name.trim().equals("")) {
            error.add("name");
        }
        try {
            number = Integer.parseInt(numberView.getText().toString());
        } catch (NumberFormatException e) {
            error.add("number");
        }

        if (event.getType().equals(MASS_START)) {
            startTime = event.getTime();
        } else {
            Spinner unitSelect = (Spinner) getView().findViewById(
                    R.id.startlist_input_starttime_unit);
            unit = (OFFSET_UNIT) unitSelect.getSelectedItem();

            try {
                startTime = Long.parseLong(startView.getText().toString());
            } catch (NumberFormatException e) {
                error.add("start time");
            }
        }

        if (error.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder("Enter ");
            boolean first = true;
            for (String s:error) {
                if (first) first = false;
                else stringBuilder.append(", ");
                stringBuilder.append(s);
            }
            message = stringBuilder.toString();
        } else {
            switch (unit) {
                case SECOND:
                    startTime = startTime * 1000;
                    break;
                case MINUTE:
                    startTime = startTime * 60 * 1000;
                    break;
            }

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues athleteValues = new ContentValues();
            ContentValues eventValues = new ContentValues();
            athleteValues.put(Contract.Athlete.KEY_NAME, name);
            athleteValues.put(Contract.Athlete.KEY_NUMBER, number);
            eventValues.put(Contract.Startlist.KEY_EVENT, event.getId());
            eventValues.put(Contract.Startlist.KEY_STARTTIME, startTime);

            database.beginTransaction();
            try {
                long athleteId = database.insert(Contract.Athlete.TABLE_NAME, null, athleteValues);
                eventValues.put(Contract.Startlist.KEY_ATHLETE, athleteId);
                database.insert(Contract.Startlist.TABLE_NAME, null, eventValues);
                database.setTransactionSuccessful();
                message = String.format("Added %s", name);

                nameView.setText(null);
                numberView.setText(null);
                startView.setText(null);
                nameView.requestFocus();

                mListener.onStartListEntryAdded(event, athleteId);
            } catch (SQLException e) {
                message = String.format("Error adding %s", name);
            } finally {
                database.endTransaction();
            }
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnStartlistEntryAddedListener {
        void onStartListEntryAdded(Event event, long athleteId);
    }
}