package org.asbjorjo.splittimer;

import android.app.Application;

import org.asbjorjo.splittimer.data.Athlete;
import org.asbjorjo.splittimer.data.Event;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class SplitTimerApplication extends Application {
    private Event event;
    private Athlete reference;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public Athlete getReference() {return reference;}
    public void setReference(Athlete reference) {this.reference = reference;}
    public Event getEvent() {return event;}
    public void setEvent(Event event) {this.event = event;}
}
