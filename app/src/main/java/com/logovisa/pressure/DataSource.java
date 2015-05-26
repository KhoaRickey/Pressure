package com.logovisa.pressure;

/**
 * Created by Khoa Rickey on 5/26/2015.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_ROOM };

    public DataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SavedData createSave(String room) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ROOM, room);
        long insertId = database.insert(SQLiteHelper.TABLE_PRESSURE, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_PRESSURE,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        SavedData newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(SavedData comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_PRESSURE, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<SavedData> getAllComments() {
        List<SavedData> comments = new ArrayList<SavedData>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_PRESSURE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SavedData comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }

    private SavedData cursorToComment(Cursor cursor) {
        SavedData pS = new SavedData();
        pS.setId(cursor.getLong(0));
        pS.setRoom(cursor.getString(1));
        return pS;
    }
}

