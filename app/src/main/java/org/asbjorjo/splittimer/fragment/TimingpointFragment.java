package org.asbjorjo.splittimer.fragment;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimingpointFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private long eventId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        dbHelper = DbHelper.getInstance(getActivity());

        getActivity().findViewById(R.id.timingpoint_input_button).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.timingpoint_fragment, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.timingpoint_input_button) addTimingpoint();
    }

    public void addTimingpoint() {
        String message;

        EditText text = (EditText) getActivity().findViewById(R.id.timingpoint_input_description);
        String description = text.getText().toString();

        if (description.trim().equals("")) {
            message = "Enter description";

            getActivity().setResult(RESULT_CANCELED);
        } else {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Contract.Timingpoint.KEY_EVENT, eventId);
            values.put(Contract.Timingpoint.KEY_DESCRIPTION, description);
            values.put(Contract.Timingpoint.KEY_POSITION, DbUtils.getTimingpointCountForEvent(
                    eventId, dbHelper
            ));
            database.insert(Contract.Timingpoint.TABLE_NAME, null, values);

            message = String.format("Added %s", description);

            text.setText(null);

            TimingpointListFragment timingpointListFragment = (TimingpointListFragment)
                    getFragmentManager().findFragmentById(R.id.intermediate_list);

            if (timingpointListFragment != null) timingpointListFragment.updateList();

            getActivity().setResult(RESULT_OK);
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}