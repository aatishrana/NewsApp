package com.aatishrana.newsapp;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aatishrana.data.database.DbOpenHelper;
import com.aatishrana.data.NewsRepositoryImpl;
import com.aatishrana.data.network.ApiClient;
import com.aatishrana.data.network.ApiInterface;
import com.aatishrana.newsapp.dialogs.FilterDialog;
import com.aatishrana.newsapp.dialogs.SortDialog;
import com.example.Keys;
import com.example.NewsItem;
import com.example.repository.NewsRepository;
import com.example.usecase.GetFilteredNews;
import com.example.usecase.GetFilteredNewsOptions;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NewsListAdapter.NewsListClickListener,
        FilterDialog.FilterDialogListener, SortDialog.SortDialogListener
{
    RecyclerView recyclerView;
    NewsListAdapter adapter;
    NewsRepositoryImpl repository;

    GetFilteredNewsOptions useCaseOption;
    List<NewsItem> data;
    int count = 0;
    boolean forceRefresh = true;
    final String FORCE_REFRESH = "force_refresh";

    FilterDialog filterDialog;
    SortDialog sortDialog;

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
        useCaseOption = new GetFilteredNewsOptions();
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
        refresh(useCaseOption);
    }

    private void refresh(GetFilteredNewsOptions options)
    {
        useCaseOption = options;
        repository.getLatestNews(useCaseOption)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewsItem>>()
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
                    public void onNext(List<NewsItem> newsItem)
                    {
                        data.addAll(newsItem);
                        adapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_sort)
        {

            if (sortDialog == null)
                sortDialog = new SortDialog(MainActivity.this);
            sortDialog.show();
            return true;
        }

        if (id == R.id.action_filter)
        {
            if (filterDialog == null)
                filterDialog = new FilterDialog(MainActivity.this);
            filterDialog.show();
            return true;
        }

        if (id == R.id.action_search)
        {
            Toast.makeText(this, "Search is Clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int position, NewsItem newsItem)
    {

    }

    @Override
    public void onFilter(String key, String value)
    {
        data.clear();
        count = 0;
        adapter.notifyDataSetChanged();

        useCaseOption.setFilter(true);
        useCaseOption.setFilterKey(key);
        useCaseOption.setFilterValue(value);
        refresh(useCaseOption);

        this.filterDialog.dismiss();
        this.filterDialog.cancel();
    }

    @Override
    public void onSortAsc(String key)
    {
        data.clear();
        count = 0;
        adapter.notifyDataSetChanged();

        useCaseOption.setSort(true);
        useCaseOption.setSortKey(key);
        useCaseOption.setSortTypeDesc(false);
        refresh(useCaseOption);

        this.sortDialog.dismiss();
        this.sortDialog.cancel();
    }

    @Override
    public void onSortDesc(String key)
    {
        data.clear();
        count = 0;
        adapter.notifyDataSetChanged();

        useCaseOption.setSort(true);
        useCaseOption.setSortKey(key);
        useCaseOption.setSortTypeDesc(true);
        refresh(useCaseOption);

        this.sortDialog.dismiss();
        this.sortDialog.cancel();
    }
}
