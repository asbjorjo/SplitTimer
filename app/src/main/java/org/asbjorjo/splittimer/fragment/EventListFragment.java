package org.asbjorjo.splittimer.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFragment extends ListFragment {
    private OnEventSelectedListener mListener;
    private DbHelper dbHelper;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dbHelper = DbHelper.getInstance(getActivity());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        dbHelper = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onEventSelected(id);
    }

    public void updateList() {
        final String[] from = {
                Contract.Event.KEY_NAME,
                Contract.Event.KEY_DATE
        };
        final int[] to = {
                R.id.list_event_name,
                R.id.list_event_date
        };

        Cursor eventCursor = DbUtils.getEvents(dbHelper);
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(getActivity(), R.layout.event_list_item, eventCursor,
                    from, to, 0);
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if (columnIndex == cursor.getColumnIndex(Contract.Event.KEY_DATE)) {
                        long date = cursor.getLong(columnIndex);
                        TextView textView = (TextView) view;
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(date);

                        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

                        textView.setText(df.format(cal.getTime()));
                        return true;
                    }
                    return false;
                }
            });
            setListAdapter(adapter);
        } else {
            Cursor oldCursor = adapter.swapCursor(eventCursor);
            oldCursor.close();
        }
    }

    public interface OnEventSelectedListener {
        void onEventSelected(long eventId);
    }
}