package org.asbjorjo.splittimer.db;

import android.provider.BaseColumns;

/**
 * Created by AJohansen2 on 11/28/2016.
 */

public class SplitTimerContract {
    private SplitTimerContract() {}

    public static class Event implements BaseColumns {
        public static final String TABLE_NAME = "event";
        public static final String COLUMN_NAME = "name";
    }
    public static class Athlete implements BaseColumns {
        public static final String TABLE_NAME = "athlete";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_STARTTIME = "starttime";
        public static final String COLUMN_EVENT = "event";
    }
    public static class Intermediate implements BaseColumns {
        public static final String TABLE_NAME = "intermediate";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_ATHLETE = "athlete";
    }
}
