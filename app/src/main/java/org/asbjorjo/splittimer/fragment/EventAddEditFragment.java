package org.asbjorjo.splittimer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.asbjorjo.splittimer.R;
import org.asbjorjo.splittimer.model.Event;

import static org.asbjorjo.splittimer.SplitTimerConstants.KEY_ACTIVE_EVENT;

/**
 * Created by AJohansen2 on 12/22/2016.
 */

public class EventAddEditFragment extends Fragment {
    private Event event;

    public static EventAddEditFragment newInstance(Event event) {
        EventAddEditFragment fragment = new EventAddEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(KEY_ACTIVE_EVENT, event);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            event = getArguments().getParcelable(KEY_ACTIVE_EVENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.event_addedit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (event.getId() <= 0) {
            view.findViewById(R.id.event_button_edit).setVisibility(View.GONE);
        }
    }
}
