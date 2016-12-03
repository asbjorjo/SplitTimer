package org.asbjorjo.splittimer.db;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.text.MessageFormat;

import static org.asbjorjo.splittimer.db.Contract.Athlete;
import static org.asbjorjo.splittimer.db.Contract.EventAthlete;
import static org.asbjorjo.splittimer.db.Contract.Intermediate;
import static org.asbjorjo.splittimer.db.Contract.IntermediateAthlete;

/**
 * Created by AJohansen2 on 12/2/2016.
 */

public class DbUtils {
    private static final String TAG = "DbUtils";

    public static int getTimingpointCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Intermediate.TABLE_NAME, Intermediate.KEYS,
                Intermediate.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getAthleteCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(EventAthlete.TABLE_NAME, EventAthlete.KEYS,
                EventAthlete.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static Cursor getTimingpointsForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Intermediate.TABLE_NAME, Intermediate.KEYS,
                Intermediate.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, Intermediate.DEFAULT_SORT_ORDER);
        return cursor;
    }

    public static Cursor getAthletesForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(EventAthlete.TABLE_NAME + " JOIN "
                + Athlete.TABLE_NAME + " ON " + Athlete._ID + " = "
                + EventAthlete.KEY_ATHLETE);
        queryBuilder.appendWhere(EventAthlete.KEY_EVENT + " = ?");
        String query = queryBuilder.buildQuery(null, null, null, null,
                EventAthlete.DEFAULT_SORT_ORDER, null);
        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(eventId)});
        return cursor;
    }

    public static Cursor getStandingsAtPoint(long timingpointId, long referenceAthlete,
                                             DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String queryTemplate = "SELECT " + IntermediateAthlete.TABLE_NAME + "." +
                IntermediateAthlete.KEY_ATHLETE +
                ",(" + IntermediateAthlete.KEY_TIMESTAMP + "- (SELECT " +
                IntermediateAthlete.KEY_TIMESTAMP + " FROM " + IntermediateAthlete.TABLE_NAME +
                " WHERE " + IntermediateAthlete.KEY_INTERMEDIATE + " = {0} AND " +
                IntermediateAthlete.KEY_ATHLETE + " = {1})) - (" + EventAthlete.KEY_STARTTIME +
                " - " + "(SELECT " + EventAthlete.KEY_STARTTIME + " FROM " +
                EventAthlete.TABLE_NAME + " JOIN " + Intermediate.TABLE_NAME + " ON " +
                EventAthlete.TABLE_NAME + "." + EventAthlete.KEY_EVENT + " = " +
                Intermediate.TABLE_NAME + "." + Intermediate.KEY_EVENT + " WHERE " +
                Intermediate.TABLE_NAME + "." + Intermediate._ID + " = {0} AND " +
                EventAthlete.TABLE_NAME + "." + EventAthlete.KEY_ATHLETE + " = {1})) AS delta " +
                "FROM " + IntermediateAthlete.TABLE_NAME + " JOIN " +
                EventAthlete.TABLE_NAME + " ON " + IntermediateAthlete.TABLE_NAME + "." +
                IntermediateAthlete.KEY_ATHLETE + " = " + EventAthlete.TABLE_NAME + "." +
                EventAthlete.KEY_ATHLETE + " JOIN " + Intermediate.TABLE_NAME + " ON " +
                Intermediate.TABLE_NAME + "." + Intermediate._ID + " = " +
                IntermediateAthlete.TABLE_NAME + "." + IntermediateAthlete.KEY_INTERMEDIATE +
                " WHERE " + IntermediateAthlete.TABLE_NAME + "." +
                IntermediateAthlete.KEY_INTERMEDIATE + " = {0} ORDER BY delta ASC";

        String query = MessageFormat.format(queryTemplate, Long.toString(timingpointId),
                Long.toString(referenceAthlete));

        Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query.toString(), null);
        return cursor;
    }

    public static Cursor getStandingForAthlete(long eventId, long athleteId, long referenceAthlete,
                                             DbHelper dbHelper) {
        Log.d(TAG, "eventId: " + eventId + " athleteId: " + athleteId + " referenceAthlete: " +
                referenceAthlete);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        final String ATHLETE_PREFIX = "cur";
        final String REFERENCE_PREFIX = "ref";

        final String ATHLETE_STARTLIST = ATHLETE_PREFIX + EventAthlete.TABLE_NAME;
        final String ATHLETE_RESULTS = ATHLETE_PREFIX + IntermediateAthlete.TABLE_NAME;
        final String REFERENCE_STARTLIST = REFERENCE_PREFIX + EventAthlete.TABLE_NAME;
        final String REFERENCE_RESULTS = REFERENCE_PREFIX + IntermediateAthlete.TABLE_NAME;

        String queryTemplate = "SELECT " + ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_INTERMEDIATE +
                ", (" + ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_TIMESTAMP + " - " +
                REFERENCE_RESULTS + "." + IntermediateAthlete.KEY_TIMESTAMP + ") - (" +
                ATHLETE_STARTLIST + "." + EventAthlete.KEY_STARTTIME + " - " +
                REFERENCE_STARTLIST + "." + EventAthlete.KEY_STARTTIME + ") AS diff FROM " +
                IntermediateAthlete.TABLE_NAME + " " + ATHLETE_RESULTS + "," +
                IntermediateAthlete.TABLE_NAME + " " + REFERENCE_RESULTS + " JOIN " +
                Intermediate.TABLE_NAME + " ON " +
                ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_INTERMEDIATE + " = " +
                Intermediate.TABLE_NAME + "." + Intermediate._ID + " JOIN " +
                EventAthlete.TABLE_NAME + " " + ATHLETE_STARTLIST + " ON " +
                ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_ATHLETE + " = " +
                ATHLETE_STARTLIST + "." + EventAthlete.KEY_ATHLETE + " JOIN " +
                EventAthlete.TABLE_NAME + " " + REFERENCE_STARTLIST + " ON " +
                REFERENCE_RESULTS + "." + IntermediateAthlete.KEY_ATHLETE + " = " +
                REFERENCE_STARTLIST + "." + EventAthlete.KEY_ATHLETE + " WHERE " +
                Intermediate.TABLE_NAME + "." + Intermediate.KEY_EVENT + " = {0} AND " +
                ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_ATHLETE + " = {1} AND " +
                REFERENCE_RESULTS + "." + IntermediateAthlete.KEY_ATHLETE + " = {2} AND " +
                ATHLETE_RESULTS + "." + IntermediateAthlete.KEY_INTERMEDIATE + " = " +
                REFERENCE_RESULTS + "." + IntermediateAthlete.KEY_INTERMEDIATE +
                " ORDER BY " + Intermediate.TABLE_NAME + "." + Intermediate.KEY_POSITION;

        String query = MessageFormat.format(queryTemplate, Long.toString(eventId),
                Long.toString(athleteId), Long.toString(referenceAthlete));

        Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query.toString(), null);
        Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
        return cursor;
    }

    public static int getPassingsForAthlete(long id, long activeEvent, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "SELECT count(athlete_id) FROM " + IntermediateAthlete.TABLE_NAME + " JOIN " +
                Intermediate.TABLE_NAME + " ON " + IntermediateAthlete.TABLE_NAME + "." +
                IntermediateAthlete.KEY_INTERMEDIATE + " = " + Intermediate.TABLE_NAME + "." +
                Intermediate._ID + " WHERE " + Intermediate.TABLE_NAME + "." +
                Intermediate.KEY_EVENT + " = ? AND " + IntermediateAthlete.TABLE_NAME + "." +
                IntermediateAthlete.KEY_ATHLETE + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(activeEvent),
                Long.toString(id)});
        cursor.moveToFirst();

        Log.d(TAG, String.format("Found %d passings", cursor.getInt(0)));

        return cursor.getInt(0);
    }

    public static long getStartTime(long eventId, long id, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(EventAthlete.TABLE_NAME, EventAthlete.KEYS,
                EventAthlete.KEY_EVENT + " = ? AND " + EventAthlete.KEY_ATHLETE + " = ?",
                new String[]{Long.toString(eventId), Long.toString(id)}, null, null, null);
        cursor.moveToFirst();
        return cursor.getLong(cursor.getColumnIndex(EventAthlete.KEY_STARTTIME));
    }
}