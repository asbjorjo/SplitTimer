package org.asbjorjo.splittimer.data;

/**
 * Created by AJohansen2 on 12/1/2016.
 */

public class TimingValues {
    private TimingPoint timingPoint;
    private Athlete athlete;
    private long timestamp;

    public TimingValues(TimingPoint timingPoint, Athlete athlete, long timestamp) {
        this.timingPoint = timingPoint;
        this.athlete = athlete;
        this.timestamp = timestamp;
    }

    public TimingPoint getTimingPoint() {
        return timingPoint;
    }

    public void setTimingPoint(TimingPoint timingPoint) {
        this.timingPoint = timingPoint;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}