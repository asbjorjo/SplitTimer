package org.asbjorjo.splittimer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.asbjorjo.splittimer.R;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * Created by AJohansen2 on 12/22/2016.
 */

public class EventAddEditFragment extends Fragment {
    public static EventAddEditFragment newInstance(long eventId) {
        EventAddEditFragment fragment = new EventAddEditFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_ACTIVE_EVENT, eventId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_addedit, container, false);

        if (getArguments() == null && getArguments().getLong(KEY_ACTIVE_EVENT) <= 0) {
            v.findViewById(R.id.event_button_edit).setVisibility(View.GONE);
        }

        return v;
    }
}
