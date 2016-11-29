package org.asbjorjo.splittimer.db;

import android.provider.BaseColumns;

/**
 * Created by AJohansen2 on 11/28/2016.
 */

public class Contract {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SplitTimer.db";

    public static final String[] SQL_CREATE_TABLES = {
            Event.CREATE_TABLE,
            Athlete.CREATE_TABLE,
            Intermediate.CREATE_TABLE
    };
    public static final String[] SQL_DELETE_TABLES = {
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
                + KEY_DESCRIPTION + " TEXT NOT NULL"
                + KEY_EVENT + " INTEGER REFERENCES " + Event._ID
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        public static final String[] KEYS = {
                KEY_DESCRIPTION,
                KEY_EVENT
        };
    }
}
