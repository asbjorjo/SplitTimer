package org.asbjorjo.splittimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.asbjorjo.splittimer.model.Athlete;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class AthleteTableDataAdapter extends TableDataAdapter<Athlete> {
    private static final String TAG = AthleteTableDataAdapter.class.getSimpleName();
    private final int precision;
    private final String decimalFormat;
    private final String resolution;
    private final Resources res;

    public AthleteTableDataAdapter(Context context, List<Athlete> data) {
        super(context, data);

        res = getResources();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String precision = preferences.getString(
                res.getString(R.string.pref_precision_key),
                res.getString(R.string.pref_precision_default));

        this.precision = Integer.parseInt(precision);
        this.decimalFormat = "%0" + (precision.length()-1) + "d";

        this.resolution = preferences.getString(
                res.getString(R.string.pref_resolution_key),
                res.getString(R.string.pref_resolution_default));

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
        String ret = "";

        long msUnsigned = Math.abs(milliseconds);

        if (precision > 1) {
            double ms = Math.abs(milliseconds) % 1000;
            long decimals = Math.round(ms / (1000 / precision));
            ret = ret + "." + String.format(decimalFormat, decimals);
        } else {
            msUnsigned = Math.round((double)msUnsigned/1000)*1000;
        }

        long hours = TimeUnit.MILLISECONDS.toHours(msUnsigned);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(msUnsigned);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(msUnsigned);

        if (resolution.equals(res.getString(R.string.pref_resolution_hours_value))) {
            ret = String.format("%02d:%02d:%02d", hours, minutes - TimeUnit.HOURS.toMinutes(hours),
                    seconds - TimeUnit.MINUTES.toSeconds(minutes)) + ret;
        } else if (resolution.equals(res.getString(R.string.pref_resolution_minutes_value))) {
            ret = String.format("%02d:%02d", minutes,
                    seconds - TimeUnit.MINUTES.toSeconds(minutes)) + ret;
        } else if (resolution.equals(res.getString(R.string.pref_resolution_seconds_value))) {
            ret = String.format("%02d", seconds) + ret;
        }

        if (milliseconds < 0) ret = "-"+ret;

        return ret;
    }
}