package org.asbjorjo.splittimer.fragment;


import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartlistFragment extends ListFragment {
    private DbHelper dbHelper;
    private long eventId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        dbHelper = DbHelper.getInstance(getActivity());
        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final String[] from = {
                Contract.Athlete.KEY_NAME,
                Contract.Athlete.KEY_NUMBER,
                Contract.Startlist.KEY_STARTTIME
        };
        final int[] to = {
                R.id.startlist_item_name,
                R.id.startlist_item_number,
                R.id.startlist_item_starttime
        };

        View v = inflater.inflate(R.layout.startlist_fragment, container, false);
        ListAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.startlist_item,
                null, from, to, 0);
        setListAdapter(adapter);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    public void refreshData() {
        Cursor cursor = DbUtils.getAthletesForEvent(eventId, dbHelper);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();

        Cursor oldCursor = adapter.swapCursor(cursor);
        if (oldCursor != null) oldCursor.close();
    }
}