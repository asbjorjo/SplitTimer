package org.asbjorjo.splittimer.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Created by AJohansen2 on 12/2/2016.
 */

public class DbUtils {
    public static int getTimingpointCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Intermediate.TABLE_NAME, Contract.Intermediate.KEYS,
                Contract.Intermediate.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static int getAthleteCountForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.EventAthlete.TABLE_NAME, Contract.EventAthlete.KEYS,
                Contract.EventAthlete.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static Cursor getTimingpointsForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(Contract.Intermediate.TABLE_NAME, Contract.Intermediate.KEYS,
                Contract.Intermediate.KEY_EVENT + " = ?", new String[]{Long.toString(eventId)},
                null, null, Contract.Intermediate.DEFAULT_SORT_ORDER);
        return cursor;
    }

    public static Cursor getAthletesForEvent(long eventId, DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.EventAthlete.TABLE_NAME + " JOIN "
                + Contract.Athlete.TABLE_NAME + " ON " + Contract.Athlete._ID + " = "
                + Contract.EventAthlete.KEY_ATHLETE);
        queryBuilder.appendWhere(Contract.EventAthlete.KEY_EVENT + " = ?");
        String query = queryBuilder.buildQuery(null, null, null, null,
                Contract.EventAthlete.DEFAULT_SORT_ORDER, null);
        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(eventId)});
        return cursor;
    }
}