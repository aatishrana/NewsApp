package com.example.usecase;

import com.example.Options;
import com.example.executor.PostExecutionThread;
import com.example.repository.NewsRepository;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public class GetFilteredNews
{
    private NewsRepository repository;

    public GetFilteredNews(NewsRepository repository, PostExecutionThread postExecutionThread)
    {
        this.repository = repository;
    }


    public Observable buildUseCaseObservable(Options option)
    {
        GetFilteredNewsOptions filteredNewsOptions = (GetFilteredNewsOptions) option;
        return repository.getLatestNews(filteredNewsOptions);
    }
}
