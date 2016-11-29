package org.asbjorjo.splittimer;

import org.asbjorjo.splittimer.data.Athlete;

import java.util.Comparator;
import java.util.List;

/**
 * Created by AJohansen2 on 11/21/2016.
 */

public class AthleteIntermediateComparator implements Comparator<Athlete> {
    private int intermediate;

    public AthleteIntermediateComparator(int intermediate) {
        this.intermediate = intermediate;
    }

    @Override
    public int compare(Athlete athlete1, Athlete athlete2) {
        int res = 0;

        List<Long> a1Int = athlete1.getIntermediates();
        List<Long> a2Int = athlete2.getIntermediates();

        if (a1Int.size() <= intermediate || a1Int.get(intermediate) == 0) {
            res = (a2Int.size() <= intermediate || a2Int.get(intermediate) == 0) ? 0 : 1;
        }
        if (a2Int.size() <= intermediate || a2Int.get(intermediate) == 0) {
            res = -1;
        }

        if (a1Int.size() > intermediate && a2Int.size() > intermediate) {
            long diff = athlete1.calculateRelativeTime(intermediate, athlete2);
            res = Long.signum(diff);
        }

        return res;
    }
}
