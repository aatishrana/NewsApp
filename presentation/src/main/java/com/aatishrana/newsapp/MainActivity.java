package com.aatishrana.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aatishrana.data.database.DbOpenHelper;
import com.aatishrana.data.models.NewsItemNetwork;
import com.aatishrana.data.NewsRepositoryImpl;
import com.aatishrana.data.network.ApiClient;
import com.aatishrana.data.network.ApiInterface;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
{
    NewsRepositoryImpl repository;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //boiler plate
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        SqlBrite sqlBrite = new SqlBrite.Builder()
                .logger(new SqlBrite.Logger()
                {
                    @Override
                    public void log(String message)
                    {
                        Log.i("aatish", "Database : " + message);
                    }
                })
                .build();

        SQLiteOpenHelper helper = new DbOpenHelper(MainActivity.this);

        BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(true);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        repository = new NewsRepositoryImpl(apiInterface, db, connectivityManager);

    }
}
