package org.asbjorjo.splittimer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimingpointFragment extends Fragment implements View.OnClickListener {
    private OnTimingpointAddedListener mListener;

    private DbHelper dbHelper;
    private long eventId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimingpointAddedListener) {
            mListener = (OnTimingpointAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimingpointAddedListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        dbHelper = DbHelper.getInstance(getActivity());
}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.timingpoint_fragment, container, false);
        v.findViewById(R.id.timingpoint_input_button).setOnClickListener(this);

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
        if (view.getId() == R.id.timingpoint_input_button) addTimingpoint();
    }

    private void addTimingpoint() {
        String message;

        EditText text = (EditText) getView().findViewById(R.id.timingpoint_input_description);
        String description = text.getText().toString();

        if (description.trim().equals("")) {
            message = "Enter description";
        } else {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Contract.Timingpoint.KEY_EVENT, eventId);
            values.put(Contract.Timingpoint.KEY_DESCRIPTION, description);
            values.put(Contract.Timingpoint.KEY_POSITION, DbUtils.getTimingpointCountForEvent(
                    eventId, dbHelper
            ));
            long id = database.insert(Contract.Timingpoint.TABLE_NAME, null, values);

            message = String.format("Added %s", description);

            text.setText(null);

            mListener.onTimingpointAdded(id);
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnTimingpointAddedListener{
        void onTimingpointAdded(long timingpointId);
    }
}