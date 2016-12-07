package org.asbjorjo.splittimer.fragment;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditFragment extends Fragment implements View.OnClickListener {
    private OnEventAddedListener mListener;
    private DbHelper dbHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventAddedListener) {
            mListener = (OnEventAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventAddedListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = DbHelper.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.event_edit_fragment, container, false);

        Button button = (Button) v.findViewById(R.id.event_input_button);
        button.setOnClickListener(this);

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
        if (view.getId() == R.id.event_input_button) addEvent();
    }

    private void addEvent() {
        EditText textView = (EditText) getView().findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) getView().findViewById(R.id.event_input_date);

        Calendar date = Calendar.getInstance();
        date.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

        String eventName = textView.getText().toString();

        String message;

        if (eventName.trim().equals("")) {
            message = "Name missing";
            getActivity().setResult(RESULT_CANCELED);
        } else {
            ContentValues values = new ContentValues();
            values.put(Contract.Event.KEY_NAME, eventName);
            values.put(Contract.Event.KEY_DATE, date.getTimeInMillis());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            long eventId = database.insert(Contract.Event.TABLE_NAME, null, values);

            textView.setText(null);

            Intent result = new Intent();
            result.putExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, eventId);

            getActivity().setResult(RESULT_OK, result);

            EventListFragment eventListFragment = (EventListFragment) getFragmentManager().
                    findFragmentById(R.id.event_list);
            if (eventListFragment != null) eventListFragment.updateList();

            message = String.format("Added %s", eventName);
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEventAddedListener {
        void onEventAdded(long eventId);
    }
}
