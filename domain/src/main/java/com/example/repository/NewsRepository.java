package com.example.repository;

import com.example.NewsItem;
import com.example.usecase.GetFilteredNewsOptions;

import java.util.List;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public interface NewsRepository
{
    Observable<List<NewsItem>> getLatestNews(GetFilteredNewsOptions options);
}
