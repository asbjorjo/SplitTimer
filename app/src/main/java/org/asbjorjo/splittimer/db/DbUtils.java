package org.asbjorjo.splittimer.db;

import android.database.Cursor;
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

        String queryTemplate = "SELECT (" + IntermediateAthlete.KEY_TIMESTAMP + "- (SELECT " +
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
}