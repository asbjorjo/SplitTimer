package org.asbjorjo.splittimer.fragment;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;
import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEventSelectedListener} interface
 * to handle interaction events.
 * Use the {@link EventSelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventSelectFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "EventSelectFragment";

    private DbHelper dbHelper;
    private long eventId;
    private OnEventSelectedListener mListener;

    public static EventSelectFragment newInstance(long eventId) {
        EventSelectFragment esf = new EventSelectFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACTIVE_EVENT, eventId);
        esf.setArguments(args);

        return esf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getLong(KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        } else if (savedInstanceState != null) {
            eventId = savedInstanceState.getLong(KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String[] from = {
                Contract.Event.KEY_NAME
        };
        final int[] to = {
                R.id.text_dropdown
        };

        View v = inflater.inflate(R.layout.event_select_fragment, container, false);

        Spinner spinner = (Spinner) v.findViewById(R.id.event_select_spinner);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, null, from, to, 0);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = DbHelper.getInstance(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    public void updateSelection(long eventId) {
        Spinner spinner = (Spinner) getView().findViewById(R.id.event_select_spinner);

        if (eventId > 0) {
            for (int i = 0; i<spinner.getCount(); i++) {
                if (spinner.getItemIdAtPosition(i) == eventId) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    public void refreshData() {
        if (getView() != null) {
            Spinner spinner = (Spinner) getView().findViewById(R.id.event_select_spinner);
            Cursor eventCursor = DbUtils.getEvents(dbHelper);

            SimpleCursorAdapter adapter = (SimpleCursorAdapter) spinner.getAdapter();

            Cursor oldCursor = adapter.swapCursor(eventCursor);

            if (oldCursor != null) oldCursor.close();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventSelectedListener) {
            mListener = (OnEventSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
        dbHelper = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_ACTIVE_EVENT, eventId);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            eventId = savedInstanceState.getLong(KEY_ACTIVE_EVENT);
        }

        updateSelection(eventId);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mListener.onEventSelected(l);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEventSelectedListener {
        void onEventSelected(long eventId);
    }
}