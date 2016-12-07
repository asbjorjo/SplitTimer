package org.asbjorjo.splittimer;

import android.app.Activity;

/**
 * Created by AJohansen2 on 11/24/2016.
 */

public class SplitTimerConstants {
    public static final String PREFS_NAME = "org.asbjorjo.splittimer.settings";
    public static final String KEY_ACTIVE_EVENT = "org.asbjorjo.splittimer.ACTIVE_EVENT";

    public static final int ADD_EVENT = 122;
    public static final int BUILD_STARTLIST = 123;
    public static final int BUILD_INTERMEDIATES = 124;

    public static final int RESULT_ADDED = Activity.RESULT_FIRST_USER + 42;
}
