package org.asbjorjo.splittimer;

import android.app.Application;

import org.asbjorjo.splittimer.data.Athlete;
import org.asbjorjo.splittimer.data.Event;

import java.util.List;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class SplitTimerApplication extends Application {
    private List<Event> eventList;
    private Event event;
    private Athlete reference;

    public Athlete getReference() {return reference;}
    public void setReference(Athlete reference) {this.reference = reference;}
    public List<Event> getEventList() {return eventList;}
    public void setEventList(List<Event> eventList) {this.eventList = eventList;}
    public Event getEvent() {return event;}
    public void setEvent(Event event) {this.event = event;}
}
