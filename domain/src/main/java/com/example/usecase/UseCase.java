
package com.example.usecase;

import com.example.Options;
import com.example.executor.PostExecutionThread;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class UseCase
{
    private final PostExecutionThread postExecutionThread;

    private Subscription subscription = Subscriptions.empty();

    protected UseCase(PostExecutionThread postExecutionThread)
    {
        this.postExecutionThread = postExecutionThread;
    }

    protected abstract Observable buildUseCaseObservable(Options options);


    @SuppressWarnings("unchecked")
    public void execute(Subscriber useCaseSubscriber, Options options)
    {
        this.subscription = this.buildUseCaseObservable(options)
                .subscribeOn(Schedulers.io())
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);
    }


    public void unsubscribe()
    {
        if (!subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }
}
