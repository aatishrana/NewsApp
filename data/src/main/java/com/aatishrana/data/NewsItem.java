package com.aatishrana.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsItem
{
    @SerializedName("ID")
    private long id;

    @SerializedName("TITLE")
    private String title;

    @SerializedName("URL")
    private String url;

    @SerializedName("PUBLISHER")
    private String publisher;

    @SerializedName("CATEGORY")
    private String category;

    @SerializedName("HOSTNAME")
    private String hostname;

    @SerializedName("TIMESTAMP")
    private long timestamp;

    public long getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUrl()
    {
        return url;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public String getCategory()
    {
        return category;
    }

    public String getHostname()
    {
        return hostname;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString()
    {
        return "NewsItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", category='" + category + '\'' +
                ", hostname='" + hostname + '\'' +
                '}';
    }
}
