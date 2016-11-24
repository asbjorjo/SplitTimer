package org.asbjorjo.splittimer;

import java.util.Comparator;

/**
 * Created by AJohansen2 on 11/21/2016.
 */

public class AthleteIntermediateComparator implements Comparator<Athlete> {
    private int intermediate;

    AthleteIntermediateComparator(int intermediate) {
        this.intermediate = intermediate;
    }

    @Override
    public int compare(Athlete o1, Athlete o2) {
        int res = 0;

        if (o1.intermediates.size() <= intermediate || o1.intermediates.get(intermediate) == 0) {
            res = (o2.intermediates.size() <= intermediate || o2.intermediates.get(intermediate) == 0) ? 0 : 1;
        }
        if (o2.intermediates.size() <= intermediate || o2.intermediates.get(intermediate) == 0) {
            res = -1;
        }

        if (o1.intermediates.size() > intermediate && o2.intermediates.size() > intermediate) {
            long diff = o1.calculateRelativeTime(intermediate, o2);
            res = Long.signum(diff);
        }

        return res;
    }
}
