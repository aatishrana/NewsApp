package com.example.usecase;

import com.example.executor.PostExecutionThread;
import com.example.executor.ThreadExecutor;
import com.example.repository.NewsRepository;

import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public class GetFilteredNews extends UseCase
{
    private NewsRepository repository;

    public GetFilteredNews(NewsRepository repository, ThreadExecutor threadExecutor,
                           PostExecutionThread postExecutionThread)
    {
        super(threadExecutor, postExecutionThread);
        this.repository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable()
    {
        return repository.getLatestNews();
    }
}
