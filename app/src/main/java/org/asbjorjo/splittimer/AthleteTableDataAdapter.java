package org.asbjorjo.splittimer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by AJohansen2 on 11/20/2016.
 */

public class AthleteTableDataAdapter extends TableDataAdapter<Athlete> {
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
}
