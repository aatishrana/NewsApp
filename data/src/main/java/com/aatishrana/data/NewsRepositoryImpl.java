package com.aatishrana.data;


import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.aatishrana.data.exceptions.NoNetworkException;
import com.aatishrana.data.models.NewsItemDb;
import com.aatishrana.data.models.NewsItemNetwork;
import com.aatishrana.data.network.ApiInterface;
import com.example.Keys;
import com.example.NewsItem;
import com.example.repository.NewsRepository;
import com.example.usecase.GetFilteredNewsOptions;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.aatishrana.data.database.DbOpenHelper.StarredTable;


/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsRepositoryImpl implements NewsRepository
{
    private ApiInterface apiInterface;
    private BriteDatabase db;
    private ConnectivityManager connectivityManager;

    public NewsRepositoryImpl(ApiInterface apiInterface, BriteDatabase db, ConnectivityManager connectivityManager)
    {
        this.apiInterface = apiInterface;
        this.db = db;
        this.connectivityManager = connectivityManager;
    }

    @Override
    public Observable<List<NewsItem>> getLatestNews(final GetFilteredNewsOptions options)
    {
        return getNews(options.isForceRefresh())
                .filter(new Func1<NewsItem, Boolean>()
                {
                    @Override
                    public Boolean call(NewsItem newsItem)
                    {
                        if (options.isFilter())
                        {
                            String filterKey = options.getFilterKey();
                            if (filterKey.length() > 0)
                                switch (filterKey)
                                {
                                    case Keys.filterTitle:
                                        return newsItem.getTitle().toLowerCase().startsWith(options.getFilterValue().toLowerCase());
                                    case Keys.filterPublisher:
                                        return newsItem.getPublisher().toLowerCase().startsWith(options.getFilterValue().toLowerCase());
                                    case Keys.filterCategory:
                                        return newsItem.getCategory().toLowerCase().startsWith(options.getFilterValue().toLowerCase());
                                    default:
                                        return true;
                                }
                            else
                                return true;
                        } else
                            return true;
                    }
                }).toSortedList(new Func2<NewsItem, NewsItem, Integer>()
                {
                    @Override
                    public Integer call(NewsItem newsItem, NewsItem newsItem2)
                    {
                        if (options.isSort())
                        {
                            String sortKey = options.getSortKey();
                            if (sortKey.length() > 0)
                                switch (sortKey)
                                {
                                    case Keys.sortId:
                                        return Long.compare(newsItem.getId(), newsItem2.getId());
                                    case Keys.sortCategory:
                                        return newsItem.getCategory().toLowerCase().compareTo(newsItem2.getCategory().toLowerCase());
                                    case Keys.sortPublisher:
                                        return newsItem.getPublisher().toLowerCase().compareTo(newsItem2.getPublisher().toLowerCase());
                                    case Keys.sortTime:
                                        return Long.compare(newsItem.getTimestamp(), newsItem2.getTimestamp());
                                    case Keys.sortTitle:
                                        return newsItem.getTitle().toLowerCase().compareTo(newsItem2.getTitle().toLowerCase());
                                    default:
                                        return 0;
                                }
                            else
                                return 0;
                        } else
                            return 0;
                    }
                });
    }

    @Override
    public Observable<List<NewsItem>> getStarredNews()
    {
        String query = "SELECT * from " + StarredTable;
        return db.createQuery(StarredTable, query, null)
                .mapToList(NewsItemDb.MapToNewsItem)
                .first();
    }

    @Override
    public void starNewsItem(long id)
    {
        String query = "Select * from " + NewsItemDb.TABLE + " where " + NewsItemDb.ID + " = '" + id + "'";
        db.createQuery(NewsItemDb.TABLE, query, null)
                .mapToOne(NewsItemDb.MapToNewsItemDb)
                .first()
                .subscribe(new Observer<NewsItemDb>()
                {
                    @Override
                    public void onCompleted()
                    {
                        Log.e("aatish", "new Item starred");
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(NewsItemDb newsItemDb)
                    {
                        db.insert(StarredTable, new NewsItemDb.Builder()
                                .id(newsItemDb.getId())
                                .title(newsItemDb.getTitle())
                                .category(newsItemDb.getCategory())
                                .hostname(newsItemDb.getHostname())
                                .publisher(newsItemDb.getPublisher())
                                .url(newsItemDb.getUrl())
                                .timestamp(newsItemDb.getTimestamp())
                                .build(), SQLiteDatabase.CONFLICT_REPLACE);
                    }
                });
    }

    private Observable<NewsItem> getNews(boolean forceRefresh)
    {
        if (forceRefresh)
        {
            return Observable.concat(getNewsFromDb(), getNewsFromApi()
                    .onErrorResumeNext(new Func1<Throwable, Observable<? extends NewsItem>>()
                    {
                        @Override
                        public Observable<? extends NewsItem> call(Throwable throwable)
                        {
                            if (throwable instanceof NoNetworkException)
                                return getNewsFromDb();
                            else
                                return Observable.empty();
                        }
                    }));
        } else
            return getNewsFromDb();
    }

    private Observable<NewsItem> getNewsFromApi()
    {
        if (isThereInternetConnection())
            return apiInterface.getNews()
                    .flatMap(new Func1<List<NewsItemNetwork>, Observable<NewsItem>>()
                    {
                        @Override
                        public Observable<NewsItem> call(List<NewsItemNetwork> newsItemNetworks)
                        {
                            return Observable.from(newsItemNetworks).map(new Func1<NewsItemNetwork, NewsItem>()
                            {
                                @Override
                                public NewsItem call(NewsItemNetwork newsItemNetwork)
                                {
                                    saveNewsItemInDb(newsItemNetwork);
                                    return new NewsItem(newsItemNetwork.getId(),
                                            newsItemNetwork.getTitle(),
                                            newsItemNetwork.getUrl(),
                                            newsItemNetwork.getPublisher(),
                                            newsItemNetwork.getCategory(),
                                            newsItemNetwork.getHostname(),
                                            newsItemNetwork.getTimestamp());
                                }
                            });

                        }
                    });
        else
            return Observable.error(new NoNetworkException());
    }

    private void saveNewsItemInDb(NewsItemNetwork newsItemNetwork)
    {
        db.insert(NewsItemDb.TABLE, new NewsItemDb.Builder()
                .id(newsItemNetwork.getId())
                .title(newsItemNetwork.getTitle())
                .category(newsItemNetwork.getCategory())
                .hostname(newsItemNetwork.getHostname())
                .publisher(newsItemNetwork.getPublisher())
                .url(newsItemNetwork.getUrl())
                .timestamp(newsItemNetwork.getTimestamp())
                .build(), SQLiteDatabase.CONFLICT_REPLACE);
    }

    private Observable<NewsItem> getNewsFromDb()
    {
        String query = "SELECT * from " + NewsItemDb.TABLE;
        return db.createQuery(NewsItemDb.TABLE, query, null)
                .mapToList(NewsItemDb.MapToNewsItemDb)
                .first()
                .flatMap(new Func1<List<NewsItemDb>, Observable<NewsItem>>()
                {
                    @Override
                    public Observable<NewsItem> call(List<NewsItemDb> newsItemDbs)
                    {
                        return Observable.from(newsItemDbs).map(new Func1<NewsItemDb, NewsItem>()
                        {
                            @Override
                            public NewsItem call(NewsItemDb newsItemDb)
                            {
                                return new NewsItem(newsItemDb.getId(),
                                        newsItemDb.getTitle(),
                                        newsItemDb.getUrl(),
                                        newsItemDb.getPublisher(),
                                        newsItemDb.getCategory(),
                                        newsItemDb.getHostname(),
                                        newsItemDb.getTimestamp());
                            }
                        });

                    }
                });
    }

    private boolean isThereInternetConnection()
    {
        boolean isConnected;
        NetworkInfo networkInfo = this.connectivityManager.getActiveNetworkInfo();
        isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());
        return isConnected;
    }
}
