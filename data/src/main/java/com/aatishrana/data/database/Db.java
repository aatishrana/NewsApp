package com.aatishrana.data.database;

import android.database.Cursor;

/**
 * Created by Aatish on 9/10/2017.
 */

public class Db
{
    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static String getString(Cursor cursor, String columnName)
    {
        if (!cursor.isNull(cursor.getColumnIndex(columnName)))
            return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
        else
            return "";
    }

    public static boolean getBoolean(Cursor cursor, String columnName)
    {
        return getInt(cursor, columnName) == BOOLEAN_TRUE;
    }

    public static long getLong(Cursor cursor, String columnName)
    {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static int getInt(Cursor cursor, String columnName)
    {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    private Db()
    {
        throw new AssertionError("No instances.");
    }
}
