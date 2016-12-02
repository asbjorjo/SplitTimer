package org.asbjorjo.splittimer;

import java.util.Comparator;

/**
 * Created by AJohansen2 on 12/2/2016.
 */

public class TableAthlete {
    private long id;
    private String name;
    private int number;
    private long[] times;

    public TableAthlete(long id, String name, int number, long[] times) {
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

    public static class TableAthleteNameComparator implements Comparator<TableAthlete> {
        @Override
        public int compare(TableAthlete o1, TableAthlete o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public static class TableAthleteNumberComparator implements Comparator<TableAthlete> {
        @Override
        public int compare(TableAthlete o1, TableAthlete o2) {
            return Integer.compare(o1.getNumber(), o2.getNumber());
        }
    }

    public static class TableAthleteTimeComparator implements Comparator<TableAthlete> {
        private int time;

        public TableAthleteTimeComparator(int time) {
            this.time = time;
        }

        @Override
        public int compare(TableAthlete o1, TableAthlete o2) {
            return Long.compare(o1.getTimes()[time], o2.getTimes()[time]);
        }
    }
}
