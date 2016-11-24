package org.asbjorjo.splittimer;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class for holding information about an Athlete and intermediate timings.
 *
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 */

public class Athlete {
    /**
     * Athlete name
     */
    String name;
    /**
     * Start number
     */
    int number;
    /**
     * Starting time in milliseconds, relative or absolute.
     */
    long startTime;
    /**
     * Array of intermediate times in milliseconds, absolute.
     */
    ArrayList<Long> intermediates;

    /**
     * Constructor for creating a sane Athlete instance.
     *
     * @param name Name
     * @param number Start number
     * @param startTime Start time in milliseconds from some offset
     */
    Athlete(String name, int number, long startTime) {
        this.name = name;
        this.number = number;
        this.startTime = startTime;
        intermediates = new ArrayList<>();
    }

    /**
     * Calculate distance in milliseconds to reference Athlete at give intermediate content_timing point,
     * relative to distance at start.
     *
     * A negative value suggests current Athlete is ahead of reference.
     *
     * @param intermediate Timing point
     * @param reference Reference Athlete
     * @return Difference at intermediate content_timing point in milliseconds
     */
    public long calculateRelativeTime(int intermediate, Athlete reference) {
        long diffAtStart = this.startTime - reference.startTime;
        long diffAtInt = this.intermediates.get(intermediate) - reference.intermediates.get(intermediate);
        return diffAtInt - diffAtStart;
    }

    public static class NameComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            return athlete1.name.compareTo(athlete2.name);
        }
    }

    public static class NumberComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete athlete1, Athlete athlete2) {
            return Long.compare(athlete1.number, athlete2.number);
        }
    }

    public static class StartComparator implements Comparator<Athlete> {
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