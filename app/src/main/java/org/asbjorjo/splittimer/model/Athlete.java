package org.asbjorjo.splittimer.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class Athlete {
    private long id;
    private String name;
    private int number;
    private long[] times;

    public Athlete(long id, String name, int number, long[] times) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.times = times;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long[] getTimes() {
        return times;
    }

    public void setTimes(long[] times) {
        this.times = times;
    }

    @Override
    public String toString() {
        String sb = "TableAthlete{" + "id=" + id +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", times=" + Arrays.toString(times) +
                '}';
        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Athlete athlete = (Athlete) o;
        return id == athlete.id &&
                number == athlete.number &&
                Objects.equals(name, athlete.name) &&
                Arrays.equals(times, athlete.times);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, number, times);
    }

    public static class TableAthleteNameComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete o1, Athlete o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static class TableAthleteNumberComparator implements Comparator<Athlete> {
        @Override
        public int compare(Athlete o1, Athlete o2) {
            return Integer.compare(o1.getNumber(), o2.getNumber());
        }
    }

    public static class TableAthleteTimeComparator implements Comparator<Athlete> {
        private final int time;

        public TableAthleteTimeComparator(int time) {
            this.time = time;
        }

        @Override
        public int compare(Athlete o1, Athlete o2) {
            return Long.compare(o1.getTimes()[time], o2.getTimes()[time]);
        }
    }
}