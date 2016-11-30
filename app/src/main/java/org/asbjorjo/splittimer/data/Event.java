package org.asbjorjo.splittimer.data;

import java.util.List;

/**
 * Class for Event data, so a user can add several events with corresponding timing points and
 * list of athletes.
 */

public class Event {
    private long id;
    /**
     * Name of event.
     */
    private String name;
    /**
     * List of intermediate timing points.
     */
    private List<String> intermediates;
    /**
     * List of Athletes participating in this Event.
     */
    private List<Athlete> athletes;

    /**
     * Constructor for simplicity.
     *
     * @param name Name of event.
     */
    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public List<String> getIntermediates() {
        return intermediates;
    }

    public void setIntermediates(List<String> intermediates) {
        this.intermediates = intermediates;
    }

    public List<Athlete> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<Athlete> athletes) {
        this.athletes = athletes;
    }

}
