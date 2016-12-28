package org.asbjorjo.splittimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asbjorjo.splittimer.model.Athlete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class AthleteTableDataAdapter extends TableDataAdapter<Athlete> {
    private static final String TAG = AthleteTableDataAdapter.class.getSimpleName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("mm:ss");
    private final int timingPrecision;

    public AthleteTableDataAdapter(Context context, List<Athlete> data) {
        super(context, data);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String precision = preferences.getString("timingprecision", Integer.toString(1));
        timingPrecision = Integer.parseInt(precision);
        Log.d(TAG, Integer.toString(timingPrecision));
    }

    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        Athlete athlete = getRowData(rowIndex);
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

    private View renderName(Athlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(athlete.getName());
        return view;
    }

    private View renderNumber(Athlete athlete) {
        TextView view = new TextView(getContext());
        view.setText(Long.toString(athlete.getNumber()));
        return view;
    }

    private View renderTime(Athlete athlete, int time) {
        TextView view = new TextView(getContext());

        if (time < athlete.getTimes().length &&
                athlete.getTimes()[time] < Long.MAX_VALUE)
            view.setText(formatTime(athlete.getTimes()[time]));
        else
            view.setText("");

        return view;
    }

    private String formatTime(long milliseconds) {
        String ret;

        long msUnsigned = Math.abs(milliseconds);

        if (timingPrecision > 1) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(msUnsigned);
            cal.clear(Calendar.MILLISECOND);

            ret = dateFormatter.format(cal.getTime());

            long ms = Math.abs(milliseconds) % 1000;
            int decimals = Math.round(ms/(1000/timingPrecision));
            ret = ret + "." + Integer.toString(decimals);
        } else {
            ret = dateFormatter.format(new Date(Math.round((double)msUnsigned/1000)*1000));
        }

        if (milliseconds < 0) ret = "-"+ret;

        return ret;
    }
}