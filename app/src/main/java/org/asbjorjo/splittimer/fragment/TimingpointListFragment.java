package org.asbjorjo.splittimer.fragment;

import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.SplitTimerConstants;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;

import static org.asbjorjo.splittimer.SplitTimerConstants.NO_ACTIVE_EVENT;

/**
 * Created by AJohansen2 on 12/6/2016.
 */

public class TimingpointListFragment extends ListFragment {
    private DbHelper dbHelper;
    private long eventId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();

        eventId = intent.getLongExtra(SplitTimerConstants.KEY_ACTIVE_EVENT, NO_ACTIVE_EVENT);
        dbHelper = DbHelper.getInstance(getActivity());

        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timingpoint_list_fragment, container, false);
    }

    public void updateList() {
        final String[] from = {
                Contract.Timingpoint.KEY_DESCRIPTION,
                Contract.Timingpoint.KEY_POSITION
        };
        final int[] to = {
                R.id.list_timingpoint_description,
                R.id.list_timingpoint_position
        };

        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(eventId, dbHelper);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();

        if (adapter == null) {
            adapter = new SimpleCursorAdapter(getActivity(), R.layout.timingpoint_list_item,
                    timingpointCursor, from, to, 0);
            setListAdapter(adapter);
        } else {
            Cursor oldCursor = adapter.swapCursor(timingpointCursor);
            oldCursor.close();
        }
    }
}