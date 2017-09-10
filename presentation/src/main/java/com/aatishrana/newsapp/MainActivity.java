package com.aatishrana.newsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aatishrana.data.database.DbOpenHelper;
import com.aatishrana.data.NewsRepositoryImpl;
import com.aatishrana.data.network.ApiClient;
import com.aatishrana.data.network.ApiInterface;
import com.aatishrana.newsapp.dialogs.FilterDialog;
import com.aatishrana.newsapp.dialogs.SortDialog;
import com.example.NewsItem;
import com.example.usecase.GetFilteredNewsOptions;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NewsListAdapter.NewsListClickListener,
        FilterDialog.FilterDialogListener, SortDialog.SortDialogListener
{
    RecyclerView recyclerView;
    ProgressBar progressBar;
    NewsListAdapter adapter;
    NewsRepositoryImpl repository;
    ConnectivityManager connectivityManager;
    GetFilteredNewsOptions useCaseOption;
    List<NewsItem> data;
    int count = 0;
    boolean forceRefresh = true;
    boolean starredOpen = false;


    final String FORCE_REFRESH = "force_refresh";
    final String STARRED_OPEN = "starred_open";
    final String RECYCLER_LAYOUT = "recycler_layout";

    FilterDialog filterDialog;
    SortDialog sortDialog;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.activity_main_progressBar);
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter = new NewsListAdapter(data, MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        if (savedInstanceState != null)
        {
            forceRefresh = savedInstanceState.getBoolean(FORCE_REFRESH);
            starredOpen = savedInstanceState.getBoolean(STARRED_OPEN);
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }

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

        connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);


        repository = new NewsRepositoryImpl(apiInterface, db, connectivityManager);
        useCaseOption = new GetFilteredNewsOptions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FORCE_REFRESH, forceRefresh);
        outState.putBoolean(STARRED_OPEN, starredOpen);
        outState.putParcelable(RECYCLER_LAYOUT, linearLayoutManager.onSaveInstanceState());
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
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
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
                        adapter.setStarred(false);
                        adapter.notifyDataSetChanged();
                    }
                });

    }

    public void getStarredItems()
    {
        resetData();
        repository.getStarredNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewsItem>>()
                {
                    @Override
                    public void onCompleted()
                    {
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        starredOpen = true;
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
                        adapter.setStarred(true);
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
            if (!starredOpen)
            {
                if (sortDialog == null)
                    sortDialog = new SortDialog(MainActivity.this);
                sortDialog.show();
            } else showError();
            return true;
        }

        if (id == R.id.action_filter)
        {
            if (!starredOpen)
            {
                if (filterDialog == null)
                    filterDialog = new FilterDialog(MainActivity.this);
                filterDialog.show();
            } else showError();
            return true;
        }

        if (id == R.id.action_starred)
        {
            getStarredItems();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int position, NewsItem newsItem)
    {
        openWebWindow(newsItem);
    }

    @Override
    public void onLinkClick(String hostname)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://" + hostname));
        startActivity(i);
    }

    @Override
    public void onNewsItemStarred(long id)
    {
        repository.starNewsItem(id);
    }

    @Override
    public void onFilter(String key, String value)
    {
        useCaseOption.setFilter(true);
        useCaseOption.setFilterKey(key);
        useCaseOption.setFilterValue(value);
        resetData();
        refresh(useCaseOption);
        this.filterDialog.dismiss();
        this.filterDialog.cancel();
    }

    @Override
    public void onSort(String key)
    {
        useCaseOption.setSort(true);
        useCaseOption.setSortKey(key);
        resetData();
        refresh(useCaseOption);
        this.sortDialog.dismiss();
        this.sortDialog.cancel();
    }

    @Override
    public void onBackPressed()
    {
        if (starredOpen)
        {
            starredOpen = false;
            resetData();
            useCaseOption.setForceRefresh(false);
            refresh(useCaseOption);
        } else
        {
            super.onBackPressed();
        }
    }

    private void showError()
    {
        Toast.makeText(MainActivity.this, "Switch to normal view, to sort/filter data", Toast.LENGTH_SHORT).show();
    }

    private void openWebWindow(NewsItem newsItem)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(newsItem.getHostname());

        WebView wv = new WebView(this);
        wv.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // load online by default

        if (!isNetworkAvailable())
        { // loading offline
            wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }

        wv.loadUrl(newsItem.getUrl());
        wv.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void resetData()
    {
        data.clear();
        count = 0;
        adapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable()
    {
        boolean isConnected;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
}
