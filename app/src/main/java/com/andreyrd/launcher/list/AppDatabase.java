package com.andreyrd.launcher.list;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class AppDatabase {

    public static class Columns implements BaseColumns {
        public static final String TABLE_NAME = "apps";
        public static final String COLUMN_NAME_PACKAGE = "package";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_INTENT = "intent";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Columns.TABLE_NAME + " (" +
                    Columns._ID + " INTEGER PRIMARY KEY," +
                    Columns.COLUMN_NAME_PACKAGE + " TEXT NOT NULL UNIQUE," +
                    Columns.COLUMN_NAME_NAME + " TEXT," +
                    Columns.COLUMN_NAME_INTENT + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Columns.TABLE_NAME;

    public static class Helper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "apps.db";

        public Helper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Delete and recreate the database when the schemea changes
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
