package org.asbjorjo.splittimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import org.asbjorjo.splittimer.data.Athlete;
import org.asbjorjo.splittimer.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJohansen2 on 11/30/2016.
 */

public class AthleteDataSource {
    private DbHelper dbHelper;
    private SQLiteDatabase database;

    public AthleteDataSource(Context context) {
        dbHelper = DbHelper.getInstance(context);
    }

    public Athlete saveAthlete(Athlete athlete) {
        ContentValues values = new ContentValues();
        values.put(Contract.Athlete.KEY_NAME, athlete.getName());
        values.put(Contract.Athlete.KEY_NUMBER, athlete.getNumber());
        athlete.setId(database.insert(Contract.Athlete.TABLE_NAME, null, values));
        return athlete;
    }

    public List<Athlete> getAllAthletes() {
        List<Athlete> list = new ArrayList<>();

        Cursor cursor = database.query(Contract.Athlete.TABLE_NAME, Contract.Athlete.KEYS,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Athlete athlete = new Athlete(
                    cursor.getString(cursor.getColumnIndex(Contract.Athlete.KEY_NAME)),
                    cursor.getInt(cursor.getColumnIndex(Contract.Athlete.KEY_NUMBER)));
            list.add(athlete);
        }

        return list;
    }

    public List<Athlete> getStartlist(Event event) {
        List<Athlete> list = new ArrayList<>();

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.Athlete.TABLE_NAME + "," + Contract.EventAthlete.TABLE_NAME);
        queryBuilder.query(database, Contract.Athlete.KEYS, null, null, null, null, Contract.EventAthlete.DEFAULT_SORT_ORDER);

        return list;
    }
}