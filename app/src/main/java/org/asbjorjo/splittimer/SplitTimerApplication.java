package org.asbjorjo.splittimer;

import android.app.Application;

/**
 * Created by AJohansen2 on 11/23/2016.
 */

public class SplitTimerApplication extends Application {
    private long activeEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.activeEvent = 0;
    }

    public long getActiveEvent() {
        return activeEvent;
    }

    public void setActiveEvent(long activeEvent) {
        this.activeEvent = activeEvent;
    }
}
