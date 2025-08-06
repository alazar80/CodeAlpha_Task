package com.example.collegealertapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * - Table/column constants for safety
 * - try-with-resources for Cursor
 * - Returns List<Event> directly
 * - Closes DB after each operation
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "college_alert.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE_EVENTS = "events";
    public static final String COL_ID       = "id";
    public static final String COL_TITLE    = "title";
    public static final String COL_DESC     = "description";
    public static final String COL_TIME     = "event_time";
    public static final String COL_NOTIF    = "notified";

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_TITLE + " TEXT NOT NULL," +
                COL_DESC  + " TEXT," +
                COL_TIME  + " INTEGER NOT NULL," +
                COL_NOTIF + " INTEGER DEFAULT 0" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public long addEvent(String title, String desc, long timeMs) {
        SQLiteDatabase db    = getWritableDatabase();
        ContentValues   cv   = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_DESC,  desc);
        cv.put(COL_TIME,  timeMs);
        long id = db.insert(TABLE_EVENTS, null, cv);
        db.close();
        return id;
    }

    public List<Event> getAllEventsList() {
        List<Event> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(
                TABLE_EVENTS,
                null,
                null,
                null,
                null,
                null,
                COL_TIME + " ASC"
        )) {
            while (c.moveToNext()) {
                long id        = c.getLong(c.getColumnIndexOrThrow(COL_ID));
                String title   = c.getString(c.getColumnIndexOrThrow(COL_TITLE));
                String desc    = c.getString(c.getColumnIndexOrThrow(COL_DESC));
                long timeMs    = c.getLong(c.getColumnIndexOrThrow(COL_TIME));
                list.add(new Event(id, title, desc, timeMs));
            }
        }
        db.close();
        return list;
    }

    public int markNotified(long id) {
        SQLiteDatabase db  = getWritableDatabase();
        ContentValues cv   = new ContentValues();
        cv.put(COL_NOTIF, 1);
        int rows = db.update(
                TABLE_EVENTS,
                cv,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows;
    }
    public int updateEvent(long id, String title, String desc, long timeMs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TITLE, title);
        cv.put(COL_DESC, desc);
        cv.put(COL_TIME, timeMs);
        int rows = db.update(TABLE_EVENTS, cv, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;  // number of rows affected
    }
    public Event getEventById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Event evt = null;
        try (Cursor c = db.query(
                TABLE_EVENTS,
                null,
                COL_ID + "=?",
                new String[]{ String.valueOf(id) },
                null, null, null
        )) {
            if (c.moveToFirst()) {
                String title = c.getString(c.getColumnIndexOrThrow(COL_TITLE));
                String desc  = c.getString(c.getColumnIndexOrThrow(COL_DESC));
                long   tm    = c.getLong  (c.getColumnIndexOrThrow(COL_TIME));
                evt = new Event(id, title, desc, tm);
            }
        }
        db.close();
        return evt;
    }

    public int deleteEvent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_EVENTS, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;  // number of rows deleted
    }

}
