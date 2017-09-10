package com.aatishrana.data;


import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aatishrana.data.exceptions.NoNetworkException;
import com.aatishrana.data.models.NewsItemDb;
import com.aatishrana.data.models.NewsItemNetwork;
import com.aatishrana.data.network.ApiInterface;
import com.example.NewsItem;
import com.example.repository.NewsRepository;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;


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
    public Observable<NewsItem> getLatestNews(boolean forceRefresh)
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
