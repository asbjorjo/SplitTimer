package org.asbjorjo.splittimer.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.asbjorjo.splittimer.model.Event;

/**
 * @author Asbjoern L. Johansen <asbjorjo@gmail.com>
 * @since 0.1
 */

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();

    private static DbHelper instance;

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, Contract.DATABASE_NAME, null, Contract.DATABASE_VERSION);
    }

    @Override
    public synchronized void close() {
        instance = null;
        super.close();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        if (!db.isReadOnly()) db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String SQL_CREATE_TABLE:Contract.SQL_CREATE_TABLES) {
            db.execSQL(SQL_CREATE_TABLE);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String SQL_DELETE_TABLE:Contract.SQL_DELETE_TABLES) {
            db.execSQL(SQL_DELETE_TABLE);
        }
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean clear_data() {
        if (instance != null) {
            SQLiteDatabase db = instance.getWritableDatabase();
            for (String SQL_DELETE_TABLE : Contract.SQL_DELETE_TABLES) {
                db.execSQL(SQL_DELETE_TABLE);
            }
            this.onCreate(db);
            return true;
        } else {
            return false;
        }
    }

    public Event saveEvent(Event event) {
            long id = DbUtils.saveEvent(event.getId(), event.getName(), event.getTime(),
                    event.getType().toString(), this);
            event.setId(id);
            return event;
        }

    public Event findEvent(long eventId) {
        Event event;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(true, Contract.Event.TABLE_NAME, Contract.Event.KEYS, Contract.Event._ID + " = ?",
                new String[]{Long.toString(eventId)}, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            long id = cursor.getLong(cursor.getColumnIndex(Contract.Event._ID));
            String name = cursor.getString(cursor.getColumnIndex(Contract.Event.KEY_NAME));
            long time = cursor.getLong(cursor.getColumnIndex(Contract.Event.KEY_DATE));
            String type = cursor.getString(cursor.getColumnIndex(Contract.Event.KEY_TYPE));

            cursor.close();

            event = new Event(id, name, time, type);
        } else {
            event = null;
        }

        return event;
    }
}