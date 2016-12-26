package org.asbjorjo.splittimer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;
import org.asbjorjo.splittimer.model.Event;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimingpointFragment extends Fragment implements View.OnClickListener {
    private OnTimingpointAddedListener mListener;

    private DbHelper dbHelper;
    private Event event;

    public static TimingpointFragment newInstance(Event event) {
        TimingpointFragment fragment = new TimingpointFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_ACTIVE_EVENT, event);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimingpointAddedListener) {
            mListener = (OnTimingpointAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimingpointAddedListener");
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timingpoint_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.timingpoint_input_button).setOnClickListener(this);
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
            values.put(Contract.Timingpoint.KEY_EVENT, event.getId());
            values.put(Contract.Timingpoint.KEY_DESCRIPTION, description);
            values.put(Contract.Timingpoint.KEY_POSITION, DbUtils.getTimingpointCountForEvent(
                    event.getId(), dbHelper
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