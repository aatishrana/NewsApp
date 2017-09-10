package com.example.executor;

import rx.Scheduler;

/**
 * Created by Aatish on 9/10/2017.
 */

public interface PostExecutionThread
{
    Scheduler getScheduler();
}
