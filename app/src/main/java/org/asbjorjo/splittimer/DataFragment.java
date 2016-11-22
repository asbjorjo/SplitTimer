package org.asbjorjo.splittimer;

import android.app.Fragment;
import android.os.Bundle;

import java.util.List;

/**
 * Created by AJohansen2 on 11/22/2016.
 */

public class DataFragment extends Fragment {
    private List<Athlete> athletes;
    private Athlete reference;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setRetainInstance(true);
    }

    public void setAthletes(List<Athlete> athletes) {
        this.athletes = athletes;
    }

    public List<Athlete> getAthletes() {
        return athletes;
    }

    public void setReference(Athlete reference) {
        this.reference = reference;
    }

    public Athlete getReference() {
        return reference;
    }
}
