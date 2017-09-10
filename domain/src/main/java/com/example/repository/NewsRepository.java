package com.example.repository;

import com.example.NewsItem;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public interface NewsRepository
{
    Observable<NewsItem> getLatestNews(boolean forceRefresh);
}
