package org.asbjorjo.splittimer;

import android.app.Activity;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class SplitTimerConstants {
    public static final String PREFS_NAME = "org.asbjorjo.splittimer.settings";
    public static final String KEY_ACTIVE_EVENT = "org.asbjorjo.splittimer.ACTIVE_EVENT";
    public static final String KEY_IS_EDITING = "org.asbjorjo.splittimer.IS_EDITING";
    public static final String KEY_OPTIONS_BUNDLE = "org.asbjorjo.splittimer.OPTIONS";

    public static final long NO_ACTIVE_EVENT = -1;

    public static final int ADD_EVENT = 122;
    public static final int BUILD_STARTLIST = 123;
    public static final int BUILD_INTERMEDIATES = 124;

    public static final int RESULT_ADDED = Activity.RESULT_FIRST_USER + 42;

    public enum EVENT_TYPE {
        INTERVAL_START_FIXED,
        INTERVAL_START_RELATIVE,
        MASS_START
    }

    public enum OFFSET_UNIT {
        SECOND,
        MINUTE
    }
}