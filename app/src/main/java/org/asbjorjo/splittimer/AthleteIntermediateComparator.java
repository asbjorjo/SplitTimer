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
        if (o1.intermediates[intermediate] == 0) {
            return (o2.intermediates[intermediate] == 0) ? 0 : 1;
        }
        if (o2.intermediates[intermediate] == 0) {
            return -1;
        }

        long diff = o1.calculateRelativeTime(intermediate, o2);
        return Long.signum(diff);
    }
}
