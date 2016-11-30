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
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Event saveEvent(Event event) {
        ContentValues values = eventToContentValues(event);
        long newId = database.insert(Contract.Event.TABLE_NAME, null, values);
        event.setId(newId);
        return event;
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

            Cursor intermediateCursor = database.query(Contract.Intermediate.TABLE_NAME,
                    null, Contract.Intermediate.KEY_EVENT + " = ?",
                    new String[]{Long.toString(event.getId())}, null, null,
                    Contract.Intermediate.DEFAULT_SORT_ORDER);

            event.setIntermediates(new ArrayList<String>());

            if (intermediateCursor.getCount() > 0) {
                intermediateCursor.moveToFirst();
                while (!intermediateCursor.isAfterLast()) {
                    event.getIntermediates().add(intermediateCursor.getString(
                            intermediateCursor.getColumnIndex(Contract.Intermediate.KEY_DESCRIPTION)
                    ));
                    intermediateCursor.moveToNext();
                }
            }

            eventCursor.moveToNext();
        }

        return events;
    }

    public Event addIntermediate(Event event, String intermediate) {
        ContentValues values = new ContentValues();
        values.put(Contract.Intermediate.KEY_EVENT, event.getId());
        values.put(Contract.Intermediate.KEY_DESCRIPTION, intermediate);
        if (event.getIntermediates() == null) event.setIntermediates(new ArrayList<String>());
        values.put(Contract.Intermediate.KEY_POSITION, event.getIntermediates().size());
        database.insert(Contract.Intermediate.TABLE_NAME, null, values);
        event.getIntermediates().add(intermediate);
        return event;
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
}
