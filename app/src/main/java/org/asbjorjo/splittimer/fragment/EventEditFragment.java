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
import android.widget.Toast;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants.EVENT_TYPE;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.model.Event;

import java.util.Calendar;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = EventEditFragment.class.getSimpleName();
    private OnEventEditActionListener mListener;
    private DbHelper dbHelper;
    private Event event;

    public static EventEditFragment newInstance(Event event) {
        EventEditFragment eef = new EventEditFragment();

        if (event == null) event = new Event();

        Bundle args = new Bundle();
        args.putParcelable(KEY_ACTIVE_EVENT, event);
        eef.setArguments(args);

        return eef;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = getArguments().getParcelable(KEY_ACTIVE_EVENT);
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

        return inflater.inflate(R.layout.event_edit_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.event_input_save).setOnClickListener(this);
        view.findViewById(R.id.event_input_cancel).setOnClickListener(this);
        Spinner spinner = (Spinner) view.findViewById(R.id.event_input_type);
        SpinnerAdapter adapter = new ArrayAdapter<>(getActivity(),
                R.layout.simple_textview, EVENT_TYPE.values());
        spinner.setAdapter(adapter);
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
        Log.d(TAG, "saveEvent");
        EditText textView = (EditText) getView().findViewById(R.id.event_input_name);
        DatePicker datePicker = (DatePicker) getView().findViewById(R.id.event_input_date);

        Calendar date = Calendar.getInstance();
        date.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);

        Spinner eventTypeSelect = (Spinner) getView().findViewById(R.id.event_input_type);
        EVENT_TYPE eventType = (EVENT_TYPE) eventTypeSelect.getSelectedItem();

        String eventName = textView.getText().toString().trim();

        String message;

        if (eventName.equals("")) {
            message = "Event name missing";
        } else {
            event.setName(eventName);
            event.setTime(date.getTimeInMillis());
            event.setType(eventType);

            event = dbHelper.saveEvent(event);
            message = String.format("%s saved", eventName);
            mListener.onEventSaved(event);
        }

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public interface OnEventEditActionListener {
        void onEventSaved(Event event);
        void onEventCancel();
    }
}