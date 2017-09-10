package com.aatishrana.data;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
    public Observable<NewsItem> getLatestNews()
    {
        return Observable.concat(getNewsFromDb(), getNewsFromApi())
                .filter(new Func1<NewsItem, Boolean>()
                {
                    @Override
                    public Boolean call(NewsItem newsItem)
                    {
                        return null;
                    }
                });

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
                            if (newsItemNetworks != null && !newsItemNetworks.isEmpty())
                                return Observable.from(newsItemNetworks).map(new Func1<NewsItemNetwork, NewsItem>()
                                {
                                    @Override
                                    public NewsItem call(NewsItemNetwork newsItemNetwork)
                                    {
                                        return new NewsItem(newsItemNetwork.getId(),
                                                newsItemNetwork.getTitle(),
                                                newsItemNetwork.getUrl(),
                                                newsItemNetwork.getPublisher(),
                                                newsItemNetwork.getCategory(),
                                                newsItemNetwork.getHostname(),
                                                newsItemNetwork.getTimestamp());
                                    }
                                });
                            else
                                return Observable.empty();
                        }
                    });
        else
            return Observable.empty();//todo
    }

    private Observable<NewsItem> getNewsFromDb()
    {
        String query = "SELECT * from " + NewsItemDb.TABLE + " ORDER BY " + NewsItemDb.TIMESTAMP + " DESC";
        return db.createQuery(NewsItemDb.TABLE, query, null)
                .mapToList(NewsItemDb.MapToNewsItemDb)
                .flatMap(new Func1<List<NewsItemDb>, Observable<NewsItem>>()
                {
                    @Override
                    public Observable<NewsItem> call(List<NewsItemDb> newsItemDbs)
                    {
                        if (newsItemDbs != null && !newsItemDbs.isEmpty())
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
                        else
                            return Observable.empty();
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
