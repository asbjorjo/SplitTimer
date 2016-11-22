package org.asbjorjo.splittimer;

import java.util.Comparator;

/**
 * Created by AJohansen2 on 11/20/2016.
 */

class Athlete {
    String name;
    int number;
    long startTime;

    long[] intermediates;

    Athlete(String name, int number, long startTime, int intermediates) {
        this.name = name;
        this.number = number;
        this.startTime = startTime;
        this.intermediates = new long[intermediates];
    }

    public long calculateRelativeTime(int intermediate, Athlete reference) {
        long diffAtStart = this.startTime - reference.startTime;
        long diffAtInt = this.intermediates[intermediate] - reference.intermediates[intermediate];
        return diffAtInt - diffAtStart;
    }

    static class NameComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            return athlete1.name.compareTo(athlete2.name);
        }
    }

    static class NumberComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            return Long.compare(athlete1.number, athlete2.number);
        }
    }

    static class StartComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            return Long.compare(athlete1.startTime, athlete2.startTime);
        }
    }

    @Override
    public String toString() {
        return this.number + " - " + this.name;
    }
}