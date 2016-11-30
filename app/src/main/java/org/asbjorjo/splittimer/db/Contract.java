package org.asbjorjo.splittimer.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AJohansen2 on 11/28/2016.
 */

public class Contract {
    public static final String AUTHORITY = "org.asbjorjo.splittimer.provider";
    public static final String SCHEME = "content://";
    public static final String SLASH = "/";
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "SplitTimer.db";

    /**
     * Array listing all SQL statements necessary to setup the database.
     */
    public static final String[] SQL_CREATE_TABLES = {
            Event.CREATE_TABLE,
            Athlete.CREATE_TABLE,
            Intermediate.CREATE_TABLE,
            EventAthlete.CREATE_TABLE,
            IntermediateAthlete.CREATE_TABLE
    };

    /**
     * Array listing all SQL statements necessary to clear the database.
     */
    public static final String[] SQL_DELETE_TABLES = {
            IntermediateAthlete.DELETE_TABLE,
            EventAthlete.DELETE_TABLE,
            Intermediate.DELETE_TABLE,
            Athlete.DELETE_TABLE,
            Event.DELETE_TABLE
    };

    /**
     * Private default constructor to avoid instantiation.
     */
    private Contract() {}

    public static class Event implements BaseColumns {
        /**
         * Private default constructor to avoid instantiation.
         */
        private Event() {}

        public static final String TABLE_NAME = "event";
        public static final String KEY_NAME = "eventName";

        /**
         * The content style URI.
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME);
        /**
         * The content URI for a single row. An ID must be appended.
         */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME + SLASH);
        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = KEY_NAME + " ASC";
        /**
         * The MIME type of {@link #CONTENT_URI} providing rows.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.event";
        /**
         * The MIME type of a {@link #CONTENT_URI} single row.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.event";

        /**
         * SQL statement to create the table.
         */
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL"
                +");";
        /**
         * SQL statement to drop table.
         */
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] KEYS = {
                _ID,
                KEY_NAME
        };
    }
    public static class Athlete implements BaseColumns {
        /**
         * Private default constructor to avoid instantiation.
         */
        private Athlete() {}

        public static final String TABLE_NAME = "athlete";
        public static final String KEY_NAME = "name";
        public static final String KEY_NUMBER = "number";

        /**
         * The content style URI.
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME);
        /**
         * The content URI for a single row. An ID must be appended.
         */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME + SLASH);
        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = KEY_NAME + " ASC";
        /**
         * The MIME type of {@link #CONTENT_URI} providing rows.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.athlete";
        /**
         * The MIME type of a {@link #CONTENT_URI} single row.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.athlete";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT NOT NULL,"
                + KEY_NUMBER + " INTEGER NOT NULL"
                + ");";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] KEYS = {
                _ID,
                KEY_NAME,
                KEY_NUMBER
        };
    }
    public static class Intermediate implements BaseColumns {
        /**
         * Private default constructor to avoid instantiation.
         */
        private Intermediate() {}

        public static final String TABLE_NAME = "intermediate";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_POSITION = "position";
        public static final String KEY_EVENT = "event";

        /**
         * The content style URI.
         */
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME);
        /**
         * The content URI for a single row. An ID must be appended.
         */
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + SLASH + TABLE_NAME + SLASH);
        /**
         * The default sort order for this table.
         */
        public static final String DEFAULT_SORT_ORDER = KEY_POSITION + " ASC";
        /**
         * The MIME type of {@link #CONTENT_URI} providing rows.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.intermediate";
        /**
         * The MIME type of a {@link #CONTENT_URI} single row.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.org.asbjorjo.splittimer.intermediate";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_DESCRIPTION + " TEXT NOT NULL,"
                + KEY_POSITION + " INTEGER NOT NULL,"
                + KEY_EVENT + " INTEGER REFERENCES " + Event.TABLE_NAME
                + " ON DELETE RESTRICT"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] KEYS = {
                _ID,
                KEY_DESCRIPTION,
                KEY_POSITION,
                KEY_EVENT
        };
    }
    public static class EventAthlete {
        /**
         * Private default constructor to avoid instantiation.
         */
        private EventAthlete() {}

        public static final String TABLE_NAME = "event_athlete";
        public static final String KEY_EVENT = "event_id";
        public static final String KEY_ATHLETE = "athlete_id";
        public static final String KEY_STARTTIME = "starttime";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_EVENT + " INTEGER REFERENCES " + Event.TABLE_NAME + ","
                + KEY_ATHLETE + " INTEGER REFERENCES " + Athlete.TABLE_NAME + ","
                + KEY_STARTTIME + " INTEGER NOT NULL" + ","
                + "PRIMARY KEY(" + KEY_EVENT + "," + KEY_ATHLETE + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] KEYS = {
                KEY_EVENT,
                KEY_ATHLETE,
                KEY_STARTTIME
        };
    }
    public static class IntermediateAthlete {
        /**
         * Private default constructor to avoid instantiation.
         */
        private IntermediateAthlete() {}

        public static final String TABLE_NAME = "intermediate_athlete";
        public static final String KEY_INTERMEDIATE = "intermediate_id";
        public static final String KEY_ATHLETE = "athlete_id";
        public static final String KEY_EVENT = "event_id";
        public static final String KEY_TIMESTAMP = "timestamp";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_INTERMEDIATE + " INTEGER REFERENCES " + Intermediate.TABLE_NAME + ","
                + KEY_ATHLETE + " INTEGER" + ","
                + KEY_EVENT + " INTEGER" + ","
                + KEY_TIMESTAMP + " INTEGER NOT NULL" + ","
                + "FOREIGN KEY(" + KEY_ATHLETE + "," + KEY_EVENT + ")"
                +" REFERENCES " + EventAthlete.TABLE_NAME + "("
                + EventAthlete.KEY_ATHLETE + "," + EventAthlete.KEY_EVENT + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] KEYS = {
                KEY_INTERMEDIATE,
                KEY_ATHLETE,
                KEY_TIMESTAMP
        };
    }
}