package com.aatishrana.data.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.aatishrana.data.NewsItem;
import com.aatishrana.data.database.Db;

import rx.functions.Func1;

/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsItemDb
{
    public static final String TABLE = "news_item_table";

    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String URL = "url";
    public static final String PUBLISHER = "publisher";
    public static final String CATEGORY = "category";
    public static final String HOSTNAME = "host_name";
    public static final String TIMESTAMP = "timestamp";

    private long id;
    private String title;
    private String url;
    private String publisher;
    private String category;
    private String hostname;
    private long timestamp;

    public NewsItemDb(long id, String title, String url, String publisher, String category, String hostname, long timestamp)
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


    public static final Func1<Cursor, NewsItemDb> MapToNewsItemDb = new Func1<Cursor, NewsItemDb>()
    {
        @Override
        public NewsItemDb call(Cursor cursor)
        {
            long id = Db.getLong(cursor, ID);
            String title = Db.getString(cursor, TITLE);
            String url = Db.getString(cursor, URL);
            String publisher = Db.getString(cursor, PUBLISHER);
            String category = Db.getString(cursor, CATEGORY);
            String hostname = Db.getString(cursor, HOSTNAME);
            long timestamp = Db.getLong(cursor, TIMESTAMP);

            return new NewsItemDb(id, title, url, publisher, category, hostname, timestamp);
        }
    };

    public static final Func1<Cursor, NewsItem> MapToNewsItem = new Func1<Cursor, NewsItem>()
    {
        @Override
        public NewsItem call(Cursor cursor)
        {
            long id = Db.getLong(cursor, ID);
            String title = Db.getString(cursor, TITLE);
            String url = Db.getString(cursor, URL);
            String publisher = Db.getString(cursor, PUBLISHER);
            String category = Db.getString(cursor, CATEGORY);
            String hostname = Db.getString(cursor, HOSTNAME);
            long timestamp = Db.getLong(cursor, TIMESTAMP);

            return new NewsItem(id, title, url, publisher, category, hostname, timestamp);
        }
    };

    public static final class Builder
    {
        private final ContentValues values = new ContentValues();

        public Builder id(long id)
        {
            values.put(ID, id);
            return this;
        }

        public Builder title(String title)
        {
            values.put(TITLE, title);
            return this;
        }

        public Builder url(String url)
        {
            values.put(URL, url);
            return this;
        }

        public Builder publisher(String publisher)
        {
            values.put(PUBLISHER, publisher);
            return this;
        }

        public Builder category(String category)
        {
            values.put(CATEGORY, category);
            return this;
        }

        public Builder hostname(String hostname)
        {
            values.put(HOSTNAME, hostname);
            return this;
        }

        public Builder timestamp(long timestamp)
        {
            values.put(TIMESTAMP, timestamp);
            return this;
        }

        public ContentValues build()
        {
            return values;
        }
    }
}
