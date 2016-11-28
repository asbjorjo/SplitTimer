package org.asbjorjo.splittimer.data;

import java.util.List;

/**
 * Created by AJohansen2 on 11/28/2016.
 */

public class Event {
    private String name;
    private List<String> intermediates;


    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIntermediates() {
        return intermediates;
    }

    public void setIntermediates(List<String> intermediates) {
        this.intermediates = intermediates;
    }
}
