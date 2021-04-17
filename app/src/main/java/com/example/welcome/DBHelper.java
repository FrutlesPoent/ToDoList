package com.example.welcome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1; // for check if database is old
    public static final String DATABASE_NAME = "taskDb";
    public static final String TABLE_TASKS = "contacts";

    public static final String KEY_ID = "_id";
    public static final String KEY_TASK = "task"; // our task
    public static final String KEY_TIME = "time"; // time left
    public static final String KEY_PRIORITY = "priority"; // task priority

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_TASKS + "(" + KEY_ID + " integer primary key,"
                + KEY_TASK + " text," + KEY_PRIORITY + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_TASKS);

        onCreate(db);

    }
}
