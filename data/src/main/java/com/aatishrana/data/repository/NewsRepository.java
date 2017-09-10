package com.aatishrana.data.repository;

import com.aatishrana.data.NewsItem;
import com.aatishrana.data.NewsOptions;

import java.util.List;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public interface NewsRepository
{
    Observable<List<NewsItem>> getLatestNews(NewsOptions options);

    Observable<List<NewsItem>> getStarredNews();

    void starNewsItem(long id);
}
