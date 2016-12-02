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

        // cur is athlete, ref is reference
        String queryTemplate = "SELECT cur.intermediate_id, " +
                "(cur.timestamp-ref.timestamp) - (scur.starttime-sref.starttime) AS diff" +
                " FROM intermediate_athlete cur, intermediate_athlete ref" +
                " JOIN timingpoint ON cur.intermediate_id = timingpoint._id" +
                " JOIN startlist scur ON cur.athlete_id = scur.athlete_id" +
                " JOIN startlist sref ON ref.athlete_id = sref.athlete_id" +
                " WHERE event = {0}" +
                " AND cur.athlete_id = {1} AND ref.athlete_id = {2}" +
                " AND cur.intermediate_id = ref.intermediate_id" +
                " ORDER BY timingpoint.position ASC";

        String query = MessageFormat.format(queryTemplate, Long.toString(eventId),
                Long.toString(athleteId), Long.toString(referenceAthlete));

        Log.d(TAG, query);

        Cursor cursor = database.rawQuery(query.toString(), null);
        Log.d(TAG, cursor.toString());
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
        Log.d(TAG, query);
        Cursor cursor = database.rawQuery(query, new String[]{Long.toString(activeEvent),
                Long.toString(id)});
        cursor.moveToFirst();
        Log.d(TAG, String.format("Found %d passings", cursor.getInt(0)));
        return cursor.getInt(0);
    }
}