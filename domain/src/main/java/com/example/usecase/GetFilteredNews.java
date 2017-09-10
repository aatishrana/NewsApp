package com.example.usecase;

import com.example.Options;
import com.example.executor.PostExecutionThread;
import com.example.repository.NewsRepository;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public class GetFilteredNews extends UseCase
{
    private NewsRepository repository;

    public GetFilteredNews(NewsRepository repository, PostExecutionThread postExecutionThread)
    {
        super(postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable(Options option)
    {
        GitFilteredNewsOptions filteredNewsOptions = (GitFilteredNewsOptions) option;
        return repository.getLatestNews(filteredNewsOptions.isForceRefresh());
    }
}
