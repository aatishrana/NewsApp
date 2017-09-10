package com.aatishrana.newsapp;

import com.example.executor.PostExecutionThread;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Aatish on 9/10/2017.
 */

public class UIThread implements PostExecutionThread
{
    @Override
    public Scheduler getScheduler()
    {
        return AndroidSchedulers.mainThread();
    }
}
