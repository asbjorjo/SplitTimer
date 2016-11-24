package org.asbjorjo.splittimer;

import android.app.Application;

import java.util.List;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class SplitTimerApplication extends Application {
    private List<Athlete> athleteList;
    private Athlete reference;
    private List<String> intermediates;

    public List<Athlete> getAthleteList() {return athleteList;}
    public void setAthleteList(List<Athlete> athleteList) {this.athleteList = athleteList;}
    public Athlete getReference() {return reference;}
    public void setReference(Athlete reference) {this.reference = reference;}
    public List<String> getIntermediates() {return intermediates;}
    public void setIntermediates(List<String> intermediates) {this.intermediates = intermediates;}
}
