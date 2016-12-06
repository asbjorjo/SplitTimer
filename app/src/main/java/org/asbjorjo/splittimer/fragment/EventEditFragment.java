package org.asbjorjo.splittimer.fragment;


import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private DbHelper dbHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = DbHelper.getInstance(getActivity());

        getActivity().findViewById(R.id.event_input_button).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.event_edit, container, false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.event_input_button) addEvent();
    }

    public void addEvent() {
        EditText textView = (EditText) getActivity().findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) getActivity().findViewById(R.id.event_input_date);

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
}
