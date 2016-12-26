package org.asbjorjo.splittimer.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.db.Contract;
import org.asbjorjo.splittimer.db.DbHelper;
import org.asbjorjo.splittimer.db.DbUtils;
import org.asbjorjo.splittimer.model.Event;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * Created by AJohansen2 on 12/6/2016.
 */

public class TimingpointListFragment extends ListFragment {
    private DbHelper dbHelper;
    private Event event;

    public static TimingpointListFragment newInstance(Event event) {
        TimingpointListFragment fragment = new TimingpointListFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_ACTIVE_EVENT, event);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.timingpoint_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String[] from = {
                Contract.Timingpoint.KEY_DESCRIPTION,
                Contract.Timingpoint.KEY_POSITION
        };
        final int[] to = {
                R.id.list_timingpoint_description,
                R.id.list_timingpoint_position
        };

        ListAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.timingpoint_list_item,
                null, from, to, 0);
        setListAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void refreshData() {
        Cursor timingpointCursor = DbUtils.getTimingpointsForEvent(event.getId(), dbHelper);
        CursorAdapter adapter = (CursorAdapter) getListAdapter();

        Cursor oldCursor = adapter.swapCursor(timingpointCursor);
        if (oldCursor != null) oldCursor.close();
    }
}