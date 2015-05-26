package com.logovisa.pressure;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Khoa Rickey on 5/26/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_PRESSURE = "pressure";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ROOM = "room";
    public static String COLUMN_PRESSURE;

    private static final String DATABASE_NAME = "pressure.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PRESSURE + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_ROOM
            + " text not null," + COLUMN_PRESSURE
            + " text not null);";
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRESSURE);
        onCreate(db);
    }
}
