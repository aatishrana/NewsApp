package com.aatishrana.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.aatishrana.data.NewsItem;
import com.aatishrana.data.NewsRepository;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
{
    NewsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new NewsRepository();
        repository.getLatestNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewsItem>>()
                {
                    @Override
                    public void onNext(List<NewsItem> newsItems)
                    {
                        Log.e("aatish", newsItems.toString());
                    }

                    @Override
                    public void onCompleted()
                    {
                        Log.e("aatish", "complete");
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Log.e("aatish", e.getLocalizedMessage());
                    }
                });
    }
}
