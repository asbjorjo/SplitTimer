package org.asbjorjo.splittimer.data;

/**
 * Created by AJohansen2 on 12/1/2016.
 */

public class TimingPoint {
    private long id;
    private String description;
    private int position;
    private Event event;

    private TimingPoint(String description, Event event) {
        this.description = description;
        this.event = event;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
