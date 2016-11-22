package org.asbjorjo.splittimer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by AJohansen2 on 11/20/2016.
 */

public class AthleteTableDataAdapter extends TableDataAdapter<Athlete> {
    private final static String timeFormat = "%02d:%02d";

    public AthleteTableDataAdapter(Context context, List<Athlete> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Athlete athlete = getRowData(rowIndex);
        View renderView = null;

        switch (columnIndex) {
            case 0:
                renderView = renderName(athlete);
                break;
            case 1:
                renderView = renderNumber(athlete);
                break;
            case 2:
                renderView = renderStart(athlete);
                break;
            default:
                renderView = renderIntermediate(athlete, columnIndex-3);
                break;
        }

        return renderView;
    }

    private View renderName(Athlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(athlete.name);
        return view;
    }

    private View renderNumber(Athlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(Long.toString(athlete.number));
        return view;
    }

    private View renderStart(Athlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(formatTime(athlete.startTime));
        return view;
    }
    private View renderIntermediate(Athlete athlete, int intermediate) {
        TextView view = new TextView(getContext());
        if (athlete.intermediates[intermediate] > 0) {
            MainActivity main = (MainActivity) getContext();
            view.setText(formatTime(athlete.calculateRelativeTime(intermediate, main.reference)));
        } else {
            view.setText("INT " + (intermediate+1));
        }
        return view;
    }

    private static String formatTime(long milliseconds) {
        if (milliseconds >= 0) {
            return String.format(timeFormat, TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            return String.format(timeFormat, TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                    TimeUnit.MILLISECONDS.toSeconds(-milliseconds) % TimeUnit.MINUTES.toSeconds(1));
        }
    }
}