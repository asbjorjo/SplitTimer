package org.asbjorjo.splittimer.db;

import android.provider.BaseColumns;

/**
 * Created by AJohansen2 on 11/28/2016.
 */

public class Contract {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "SplitTimer.db";

    public static final String[] SQL_CREATE_TABLES = {
            Event.CREATE_TABLE,
            Athlete.CREATE_TABLE,
            Intermediate.CREATE_TABLE,
            EventAthlete.CREATE_TABLE,
            IntermediateAthlete.CREATE_TABLE
    };
    public static final String[] SQL_DELETE_TABLES = {
            IntermediateAthlete.DELETE_TABLE,
            EventAthlete.DELETE_TABLE,
            Intermediate.DELETE_TABLE,
            Athlete.DELETE_TABLE,
            Event.DELETE_TABLE
    };

    private Contract() {}

    public static class Event implements BaseColumns {
        private Event() {}

        public static final String TABLE_NAME = "event";
        public static final String KEY_NAME = "eventName";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL"
                +");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        public static final String[] KEYS = {
                KEY_NAME
        };
    }
    public static class Athlete implements BaseColumns {
        private Athlete() {}

        public static final String TABLE_NAME = "athlete";
        public static final String KEY_NAME = "name";
        public static final String KEY_NUMBER = "number";
        public static final String KEY_STARTTIME = "starttime";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_NUMBER + " INTEGER NOT NULL,"
                + KEY_STARTTIME + " INTEGER NOT NULL"
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        public static final String[] KEYS = {
                KEY_NAME,
                KEY_NUMBER,
                KEY_STARTTIME
        };
    }
    public static class Intermediate implements BaseColumns {
        private Intermediate() {}

        public static final String TABLE_NAME = "intermediate";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_EVENT = "event";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_DESCRIPTION + " TEXT NOT NULL,"
                + KEY_EVENT + " INTEGER REFERENCES " + Event.TABLE_NAME
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        public static final String[] KEYS = {
                KEY_DESCRIPTION,
                KEY_EVENT
        };
    }
    public static class EventAthlete {
        private EventAthlete() {}

        public static final String TABLE_NAME = "event_athlete";
        public static final String KEY_EVENT = "event_id";
        public static final String KEY_ATHLETE = "athlete_id";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_EVENT + " INTEGER REFERENCES " + Event.TABLE_NAME + ","
                + KEY_ATHLETE + " INTEGER REFERENCES " + Athlete.TABLE_NAME
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }
    public static class IntermediateAthlete {
        private IntermediateAthlete() {}

        public static final String TABLE_NAME = "intermediate_athlete";
        public static final String KEY_INTERMEDIATE = "intermediate_id";
        public static final String KEY_ATHLETE = "athlete_id";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_INTERMEDIATE + " INTEGER REFERENCES " + Intermediate.TABLE_NAME + ","
                + KEY_ATHLETE + " INTEGER REFERENCES " + Athlete.TABLE_NAME
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }
}