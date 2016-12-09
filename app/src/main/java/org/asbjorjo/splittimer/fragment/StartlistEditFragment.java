package org.asbjorjo.splittimer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartlistEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = StartlistEditFragment.class.getSimpleName();

    private DbHelper dbHelper;
    private long eventId;
    private OnStartlistEntryAddedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStartlistEntryAddedListener) {
            mListener = (OnStartlistEntryAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStartlistEntryAddedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        dbHelper = DbHelper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.startlist_edit_fragment, container, false);
        v.findViewById(R.id.startlist_input_button).setOnClickListener(this);
        return v;
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
        try {
            startTime = Long.parseLong(startView.getText().toString());
        } catch (NumberFormatException e) {
            error.add("start time");
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
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues athleteValues = new ContentValues();
            ContentValues eventValues = new ContentValues();
            athleteValues.put(Contract.Athlete.KEY_NAME, name);
            athleteValues.put(Contract.Athlete.KEY_NUMBER, number);
            eventValues.put(Contract.Startlist.KEY_EVENT, eventId);
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

                mListener.onStartListEntryAdded(eventId, athleteId);
            } catch (SQLException e) {
                message = String.format("Error adding %s", name);
            } finally {
                database.endTransaction();
            }
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnStartlistEntryAddedListener {
        void onStartListEntryAdded(long eventId, long athleteId);
    }
}