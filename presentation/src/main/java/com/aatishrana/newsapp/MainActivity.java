package com.aatishrana.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aatishrana.data.database.DbOpenHelper;
import com.aatishrana.data.NewsRepositoryImpl;
import com.aatishrana.data.network.ApiClient;
import com.aatishrana.data.network.ApiInterface;
import com.example.NewsItem;
import com.example.usecase.GetFilteredNews;
import com.example.usecase.GitFilteredNewsOptions;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NewsListAdapter.NewsListClickListener
{
    RecyclerView recyclerView;
    NewsListAdapter adapter;
    NewsRepositoryImpl repository;
    GetFilteredNews useCase;
    GitFilteredNewsOptions useCaseOption;
    List<NewsItem> data;
    int count = 0;
    boolean forceRefresh = true;
    final String FORCE_REFRESH = "force_refresh";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null)
            forceRefresh = savedInstanceState.getBoolean(FORCE_REFRESH);
        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        adapter = new NewsListAdapter(data, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

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

        final ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        repository = new NewsRepositoryImpl(apiInterface, db, connectivityManager);
        useCase = new GetFilteredNews(repository, new UIThread());
        useCaseOption = new GitFilteredNewsOptions();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FORCE_REFRESH, forceRefresh);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        useCaseOption.setForceRefresh(forceRefresh);
        forceRefresh = false; // fetch latest data only on app load
        useCase.execute(new Subscriber()
        {
            @Override
            public void onCompleted()
            {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e)
            {
                e.printStackTrace();
            }

            @Override
            public void onNext(Object o)
            {
                data.add((NewsItem) o);
                adapter.notifyItemInserted(count++);
            }
        }, useCaseOption);
    }

    @Override
    public void onClick(int position, NewsItem newsItem)
    {

    }
}
