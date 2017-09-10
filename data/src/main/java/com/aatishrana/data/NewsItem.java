package com.aatishrana.data;

/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsItem
{
    private final long id;
    private final String title;
    private final String url;
    private final String publisher;
    private final String category;
    private final String hostname;
    private final long timestamp;

    public NewsItem(long id, String title, String url, String publisher, String category, String hostname, long timestamp)
    {
        this.id = id;
        this.title = title;
        this.url = url;
        this.publisher = publisher;
        this.category = category;
        this.hostname = hostname;
        this.timestamp = timestamp;
    }

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
}
