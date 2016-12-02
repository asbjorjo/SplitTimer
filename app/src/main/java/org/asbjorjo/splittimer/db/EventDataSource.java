package org.asbjorjo.splittimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.asbjorjo.splittimer.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AJohansen2 on 11/30/2016.
 */

public class EventDataSource {
    private SQLiteDatabase database;
    private DbHelper dbHelper;

    public EventDataSource(Context context) {
        dbHelper = DbHelper.getInstance(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long saveEvent(String name) {
        ContentValues values = new ContentValues();
        values.put(Contract.Event.KEY_NAME, name);
        long newId = database.insert(Contract.Event.TABLE_NAME, null, values);
        return newId;
    }

    public void deleteEvent(Event event) {
        long id = event.getId();
        database.delete(Contract.Event.TABLE_NAME, Contract.Event._ID + " = ?",
                new String[]{Long.toString(id)});
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();

        Cursor eventCursor = database.query(Contract.Event.TABLE_NAME,
                null, null, null, null, null, null);
        eventCursor.moveToFirst();
        while (!eventCursor.isAfterLast()) {
            Event event = cursorToEvent(eventCursor);

            event.setIntermediates(findIntermediateForEvent(event.getId()));

            eventCursor.moveToNext();
        }

        return events;
    }

    private List<String> findIntermediateForEvent(long eventId) {
        List<String> intermediate = new ArrayList<String>();

        Cursor intermediateCursor = database.query(Contract.Intermediate.TABLE_NAME,
                null, Contract.Intermediate.KEY_EVENT + " = ?",
                new String[]{Long.toString(eventId)}, null, null,
                Contract.Intermediate.DEFAULT_SORT_ORDER);

        if (intermediateCursor.getCount() > 0) {
            intermediateCursor.moveToFirst();
            while (!intermediateCursor.isAfterLast()) {
                intermediate.add(intermediateCursor.getString(
                        intermediateCursor.getColumnIndex(Contract.Intermediate.KEY_DESCRIPTION)
                ));
                intermediateCursor.moveToNext();
            }
        }

        return intermediate;
    }

    private int countIntermediateForEvent(long eventId) {
        Cursor intermediateCursor = database.query(Contract.Intermediate.TABLE_NAME,
                null, Contract.Intermediate.KEY_EVENT + " = ?",
                new String[]{Long.toString(eventId)}, null, null,
                Contract.Intermediate.DEFAULT_SORT_ORDER);
        return intermediateCursor.getCount();
    }

    public void addIntermediate(long eventId, String description) {
        ContentValues values = new ContentValues();
        values.put(Contract.Intermediate.KEY_EVENT, eventId);
        values.put(Contract.Intermediate.KEY_DESCRIPTION, description);
        database.beginTransaction();
        values.put(Contract.Intermediate.KEY_POSITION, countIntermediateForEvent(eventId));
        database.insert(Contract.Intermediate.TABLE_NAME, null, values);
        database.endTransaction();
    }

    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event(cursor.getString(cursor.getColumnIndex(Contract.Event.KEY_NAME)));
        event.setId(cursor.getLong(cursor.getColumnIndex(Contract.Event._ID)));
        return event;
    }

    private ContentValues eventToContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(Contract.Event.KEY_NAME, event.getName());
        return values;
    }

    public Event getEvent(long eventId) {
        Cursor cursor = database.query(Contract.Event.TABLE_NAME, Contract.Event.KEYS,
                Contract.Event._ID +" = ?", new String[]{Long.toString(eventId)}, null, null, null);
        return cursorToEvent(cursor);
    }
}
