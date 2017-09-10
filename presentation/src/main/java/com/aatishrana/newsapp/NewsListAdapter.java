package com.aatishrana.newsapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.NewsItem;

import java.util.List;

/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder>
{
    private List<NewsItem> data;
    private NewsListClickListener newsListClickListener;

    public NewsListAdapter(List<NewsItem> data, NewsListClickListener newsListClickListener)
    {
        this.data = data;
        this.newsListClickListener = newsListClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        NewsItem newItem = data.get(position);
        holder.title.setText(newItem.getTitle());
//        holder.category.setText(newItem.getCategory());
        holder.category.setText(String.valueOf(newItem.getId()));
        holder.host.setText(newItem.getHostname());
        holder.publisher.setText(newItem.getPublisher());
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title, publisher, host, category;

        public ViewHolder(View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.news_list_item_tv_title);
            publisher = (TextView) itemView.findViewById(R.id.news_list_item_tv_publisher);
            host = (TextView) itemView.findViewById(R.id.news_list_item_tv_host_name);
            category = (TextView) itemView.findViewById(R.id.news_list_item_tv_category);

            itemView.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v)
        {
            if (newsListClickListener != null)
                newsListClickListener.onClick(getAdapterPosition(), data.get(getAdapterPosition()));
        }
    }

    public interface NewsListClickListener
    {
        void onClick(int position, NewsItem newsItem);
    }
}
