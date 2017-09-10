package com.aatishrana.data;

import com.aatishrana.data.network.ApiClient;
import com.aatishrana.data.network.ApiInterface;

import java.util.List;

import rx.Observable;


/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsRepository
{
    ApiInterface apiInterface;

    public NewsRepository()
    {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    public Observable<List<NewsItem>> getLatestNews()
    {
        return apiInterface.getNews();
    }
}
