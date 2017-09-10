package com.aatishrana.data.network;

import com.aatishrana.data.models.NewsItemNetwork;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Aatish on 9/10/2017.
 */

public interface ApiInterface
{
    @GET("/newsjson")
    Observable<List<NewsItemNetwork>> getNews();
}
