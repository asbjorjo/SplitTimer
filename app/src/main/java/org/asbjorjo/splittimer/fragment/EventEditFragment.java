package org.asbjorjo.splittimer.fragment;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.Contract.Event.EVENT_TYPE;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.Calendar;

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

        v.findViewById(R.id.event_input_button).setOnClickListener(this);
        Spinner spinner = (Spinner) v.findViewById(R.id.event_input_type);
        SpinnerAdapter adapter = new ArrayAdapter<EVENT_TYPE>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, EVENT_TYPE.values());
        spinner.setAdapter(adapter);

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

        Spinner eventTypeSelect = (Spinner) getView().findViewById(R.id.event_input_type);
        EVENT_TYPE eventType = (EVENT_TYPE) eventTypeSelect.getSelectedItem();

        String eventName = textView.getText().toString();

        String message;

        if (eventName.trim().equals("")) {
            message = "Name missing";
        } else {
            ContentValues values = new ContentValues();
            values.put(Contract.Event.KEY_NAME, eventName);
            values.put(Contract.Event.KEY_DATE, date.getTimeInMillis());
            values.put(Contract.Event.KEY_TYPE, eventType.toString());

            SQLiteDatabase database = dbHelper.getWritableDatabase();
            long eventId = database.insert(Contract.Event.TABLE_NAME, null, values);

            textView.setText(null);

            Intent result = new Intent();
            result.putExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, eventId);

            EventListFragment eventListFragment = (EventListFragment) getFragmentManager().
                    findFragmentById(R.id.event_list);
            if (eventListFragment != null) eventListFragment.updateList();

            mListener.onEventAdded(eventId);

            message = String.format("Added %s", eventName);
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEventAddedListener {
        void onEventAdded(long eventId);
    }
}