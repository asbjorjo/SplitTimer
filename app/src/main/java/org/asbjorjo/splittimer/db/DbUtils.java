package org.asbjorjo.splittimer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.text.MessageFormat;

import static org.asbjorjo.splittimer.db.Contract.Athlete;
import static org.asbjorjo.splittimer.db.Contract.Result;
import static org.asbjorjo.splittimer.db.Contract.Startlist;
import static org.asbjorjo.splittimer.db.Contract.Timingpoint;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class DbUtils {
    private static final String TAG = DbUtils.class.getSimpleName();

    public static Cursor getEvents(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor eventCursor = database.query(Contract.Event.TABLE_NAME, Contract.Event.KEYS,
                null, null, null, null, Contract.Event.DEFAULT_SORT_ORDER);
        return eventCursor;
    }

    public static int getTimingpointCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Timingpoint.TABLE_NAME, Timingpoint.KEYS,
                Timingpoint.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getAthleteCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Startlist.TABLE_NAME, Startlist.KEYS,
                Startlist.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static Cursor getTimingpointsForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Timingpoint.TABLE_NAME, Timingpoint.KEYS,
                Timingpoint.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, Timingpoint.DEFAULT_SORT_ORDER);
        return cursor;
    }

    public static Cursor getAthletesForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Startlist.TABLE_NAME + " JOIN "
                + Athlete.TABLE_NAME + " ON " + Athlete._ID + " = "
                + Startlist.KEY_ATHLETE);
        queryBuilder.appendWhere(Startlist.KEY_EVENT + " = ?");
        String query = queryBuilder.buildQuery(null, null, null, null,
                Startlist.DEFAULT_SORT_ORDER, null);
        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(eventId)});
        return cursor;
    }

    public static Cursor getStandingsAtPoint(long timingpointId, long referenceAthlete,
                                             DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryTemplate = "SELECT " + Result.TABLE_NAME + "." +
                Result.KEY_ATHLETE +
                ",(" + Result.KEY_TIMESTAMP + "- (SELECT " +
                Result.KEY_TIMESTAMP + " FROM " + Result.TABLE_NAME +
                " WHERE " + Result.KEY_TIMINGPOINT + " = {0} AND " +
                Result.KEY_ATHLETE + " = {1})) - (" + Startlist.KEY_STARTTIME +
                " - " + "(SELECT " + Startlist.KEY_STARTTIME + " FROM " +
                Startlist.TABLE_NAME + " JOIN " + Timingpoint.TABLE_NAME + " ON " +
                Startlist.TABLE_NAME + "." + Startlist.KEY_EVENT + " = " +
                Timingpoint.TABLE_NAME + "." + Timingpoint.KEY_EVENT + " WHERE " +
                Timingpoint.TABLE_NAME + "." + Timingpoint._ID + " = {0} AND " +
                Startlist.TABLE_NAME + "." + Startlist.KEY_ATHLETE + " = {1})) AS delta " +
                "FROM " + Result.TABLE_NAME + " JOIN " +
                Startlist.TABLE_NAME + " ON " + Result.TABLE_NAME + "." +
                Result.KEY_ATHLETE + " = " + Startlist.TABLE_NAME + "." +
                Startlist.KEY_ATHLETE + " JOIN " + Timingpoint.TABLE_NAME + " ON " +
                Timingpoint.TABLE_NAME + "." + Timingpoint._ID + " = " +
                Result.TABLE_NAME + "." + Result.KEY_TIMINGPOINT +
                " WHERE " + Result.TABLE_NAME + "." +
                Result.KEY_TIMINGPOINT + " = {0} ORDER BY delta ASC";

        String query = MessageFormat.format(queryTemplate, Long.toString(timingpointId),
                Long.toString(referenceAthlete));

        return database.rawQuery(query, null);
    }

    /**
     *  TODO Rewrite query to use first Result.KEY_TIMESTAMP instead Startlist.KEY_STARTTIME
     */
    public static Cursor getStandingForAthlete(long eventId, long athleteId, long referenceAthlete,
                                             DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        final String ATHLETE_PREFIX = "cur";
        final String REFERENCE_PREFIX = "ref";

        final String ATHLETE_STARTLIST = ATHLETE_PREFIX + Startlist.TABLE_NAME;
        final String ATHLETE_RESULTS = ATHLETE_PREFIX + Result.TABLE_NAME;
        final String REFERENCE_STARTLIST = REFERENCE_PREFIX + Startlist.TABLE_NAME;
        final String REFERENCE_RESULTS = REFERENCE_PREFIX + Result.TABLE_NAME;

        String queryTemplate = "SELECT " + ATHLETE_RESULTS + "." + Result.KEY_TIMINGPOINT +
                ", (" + ATHLETE_RESULTS + "." + Result.KEY_TIMESTAMP + " - " +
                REFERENCE_RESULTS + "." + Result.KEY_TIMESTAMP + ") - (" +
                ATHLETE_STARTLIST + "." + Startlist.KEY_STARTTIME + " - " +
                REFERENCE_STARTLIST + "." + Startlist.KEY_STARTTIME + ") AS diff FROM " +
                Result.TABLE_NAME + " " + ATHLETE_RESULTS + "," +
                Result.TABLE_NAME + " " + REFERENCE_RESULTS + " JOIN " +
                Timingpoint.TABLE_NAME + " ON " +
                ATHLETE_RESULTS + "." + Result.KEY_TIMINGPOINT + " = " +
                Timingpoint.TABLE_NAME + "." + Timingpoint._ID + " JOIN " +
                Startlist.TABLE_NAME + " " + ATHLETE_STARTLIST + " ON " +
                ATHLETE_RESULTS + "." + Result.KEY_ATHLETE + " = " +
                ATHLETE_STARTLIST + "." + Startlist.KEY_ATHLETE + " JOIN " +
                Startlist.TABLE_NAME + " " + REFERENCE_STARTLIST + " ON " +
                REFERENCE_RESULTS + "." + Result.KEY_ATHLETE + " = " +
                REFERENCE_STARTLIST + "." + Startlist.KEY_ATHLETE + " WHERE " +
                Timingpoint.TABLE_NAME + "." + Timingpoint.KEY_EVENT + " = {0} AND " +
                ATHLETE_RESULTS + "." + Result.KEY_ATHLETE + " = {1} AND " +
                REFERENCE_RESULTS + "." + Result.KEY_ATHLETE + " = {2} AND " +
                ATHLETE_RESULTS + "." + Result.KEY_TIMINGPOINT + " = " +
                REFERENCE_RESULTS + "." + Result.KEY_TIMINGPOINT +
                " ORDER BY " + Timingpoint.TABLE_NAME + "." + Timingpoint.KEY_POSITION;

        String query = MessageFormat.format(queryTemplate, Long.toString(eventId),
                Long.toString(athleteId), Long.toString(referenceAthlete));

        return database.rawQuery(query, null);
    }

    public static int getPassingsForAthlete(long id, long activeEvent, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT count(athlete_id) FROM " + Result.TABLE_NAME + " JOIN " +
                Timingpoint.TABLE_NAME + " ON " + Result.TABLE_NAME + "." +
                Result.KEY_TIMINGPOINT + " = " + Timingpoint.TABLE_NAME + "." +
                Timingpoint._ID + " WHERE " + Timingpoint.TABLE_NAME + "." +
                Timingpoint.KEY_EVENT + " = ? AND " + Result.TABLE_NAME + "." +
                Result.KEY_ATHLETE + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(activeEvent),
                Long.toString(id)});
        cursor.moveToFirst();

        int passings = cursor.getInt(0);
        cursor.close();

        return passings;
    }

    public static long getStartTime(long eventId, long id, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Startlist.TABLE_NAME, Startlist.KEYS,
                Startlist.KEY_EVENT + " = ? AND " + Startlist.KEY_ATHLETE + " = ?",
                new String[]{Long.toString(eventId), Long.toString(id)}, null, null, null);
        cursor.moveToFirst();
        long startTime = cursor.getLong(cursor.getColumnIndex(Startlist.KEY_STARTTIME));
        cursor.close();

        return startTime;
    }
}