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

public class AthleteTableDataAdapter extends TableDataAdapter<TableAthlete> {
    private static final String TAG = "AthleteTableDataAdapter";
    private static final String timeFormat = "%02d:%02d";

    public AthleteTableDataAdapter(Context context, List<TableAthlete> data) {
        super(context, data);
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        TableAthlete athlete = getRowData(rowIndex);
        View renderView;

        switch (columnIndex) {
            case 0:
                renderView = renderName(athlete);
                break;
            case 1:
                renderView = renderNumber(athlete);
                break;
            default:
                renderView = renderTime(athlete, columnIndex-2);
                break;
        }

        return renderView;
    }

    private View renderName(TableAthlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(athlete.getName());
        return view;
    }

    private View renderNumber(TableAthlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(Long.toString(athlete.getNumber()));
        return view;
    }

    private View renderTime(TableAthlete athlete, int time) {
        TextView view = new TextView(getContext());

        if (time < athlete.getTimes().length &&
                athlete.getTimes()[time] < Long.MAX_VALUE)
            view.setText(formatTime(athlete.getTimes()[time]));
        else
            view.setText("");

        return view;
    }

    private static String formatTime(long milliseconds) {
        String ret = String.format(timeFormat, TimeUnit.MILLISECONDS.toMinutes(Math.abs(milliseconds)),
                    TimeUnit.MILLISECONDS.toSeconds(Math.abs(milliseconds)) % TimeUnit.MINUTES.toSeconds(1));
        if (milliseconds < 0) ret = "-"+ret;
        return ret;
    }
}