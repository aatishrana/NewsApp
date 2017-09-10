package com.aatishrana.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aatishrana.data.models.NewsItemDb;

/**
 * Created by Aatish on 9/10/2017.
 */

public class DbOpenHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    public static final String StarredTable = "Star_" + NewsItemDb.TABLE;


    public DbOpenHelper(Context context)
    {
        super(context, "news_app.db", null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(createNewsItemTable());
        db.execSQL(createNewsStarItemTable());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private String createNewsItemTable()
    {
        Log.i("aatish", "DataBase : creating table " + NewsItemDb.TABLE);
        return "CREATE TABLE " + NewsItemDb.TABLE + " (" +
                NewsItemDb.ID + " INTEGER PRIMARY KEY," +
                NewsItemDb.TITLE + " TEXT NOT NULL, " +
                NewsItemDb.URL + " TEXT NOT NULL, " +
                NewsItemDb.PUBLISHER + " TEXT NOT NULL, " +
                NewsItemDb.CATEGORY + " TEXT NOT NULL, " +
                NewsItemDb.HOSTNAME + " TEXT NOT NULL, " +
                NewsItemDb.TIMESTAMP + " INTEGER NOT NULL " +
                " );";
    }

    private String createNewsStarItemTable()
    {
        Log.i("aatish", "DataBase : creating table " + NewsItemDb.TABLE);
        return "CREATE TABLE " + StarredTable + " (" +
                NewsItemDb.ID + " INTEGER PRIMARY KEY," +
                NewsItemDb.TITLE + " TEXT NOT NULL, " +
                NewsItemDb.URL + " TEXT NOT NULL, " +
                NewsItemDb.PUBLISHER + " TEXT NOT NULL, " +
                NewsItemDb.CATEGORY + " TEXT NOT NULL, " +
                NewsItemDb.HOSTNAME + " TEXT NOT NULL, " +
                NewsItemDb.TIMESTAMP + " INTEGER NOT NULL " +
                " );";
    }
}
