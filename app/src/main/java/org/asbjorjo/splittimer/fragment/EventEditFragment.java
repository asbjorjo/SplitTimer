package org.asbjorjo.splittimer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract.Event.EVENT_TYPE;
import org.asbjorjo.splittimer.db.DbHelper;

import java.util.Calendar;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = EventEditFragment.class.getSimpleName();
    private OnEventEditActionListener mListener;
    private DbHelper dbHelper;
    private long eventId;

    public static EventEditFragment newInstance(long eventId) {
        EventEditFragment eef = new EventEditFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACTIVE_EVENT, eventId);
        eef.setArguments(args);

        return eef;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eventId = getArguments().getLong(KEY_ACTIVE_EVENT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventEditActionListener) {
            mListener = (OnEventEditActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventEditActionListener");
        }
        dbHelper = DbHelper.getInstance(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.event_edit_fragment, container, false);

        v.findViewById(R.id.event_input_save).setOnClickListener(this);
        v.findViewById(R.id.event_input_cancel).setOnClickListener(this);
        Spinner spinner = (Spinner) v.findViewById(R.id.event_input_type);
        SpinnerAdapter adapter = new ArrayAdapter<EVENT_TYPE>(getActivity(),
                R.layout.simple_textview, EVENT_TYPE.values());
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
        Log.d(TAG, "onClick");
        switch (view.getId()) {
            case R.id.event_input_save:
                saveEvent();
                break;
            case R.id.event_input_cancel:
                mListener.onEventCancel();
                break;
        }
    }

    private void saveEvent() {
        EditText textView = (EditText) getView().findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) getView().findViewById(R.id.event_input_date);

        Calendar date = Calendar.getInstance();
        date.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

        Spinner eventTypeSelect = (Spinner) getView().findViewById(R.id.event_input_type);
        EVENT_TYPE eventType = (EVENT_TYPE) eventTypeSelect.getSelectedItem();

        String eventName = textView.getText().toString();

        Bundle event = new Bundle();

        event.putString("name", eventName);
        event.putLong("date", date.getTimeInMillis());
        event.putString("type", eventType.toString());

        mListener.onEventSaved(event);
    }

    public interface OnEventEditActionListener {
        void onEventSaved(Bundle event);
        void onEventCancel();
    }
}