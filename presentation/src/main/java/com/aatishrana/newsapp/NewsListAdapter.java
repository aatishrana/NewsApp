package com.aatishrana.newsapp;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private boolean starred = false;

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
        holder.category.setText(newItem.getCategory());
        holder.host.setText(newItem.getHostname());
        holder.publisher.setText(newItem.getPublisher());
        if (starred)
            holder.star.setImageDrawable(ContextCompat.getDrawable(holder.star.getContext(), R.drawable.ic_star_red_24dp));
        else
            holder.star.setImageDrawable(ContextCompat.getDrawable(holder.star.getContext(), R.drawable.ic_star_border_red_24dp));
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    public void setStarred(boolean starred)
    {
        this.starred = starred;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView title, publisher, host, category;
        ImageView star;

        public ViewHolder(View itemView)
        {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.news_list_item_tv_title);
            publisher = (TextView) itemView.findViewById(R.id.news_list_item_tv_publisher);
            host = (TextView) itemView.findViewById(R.id.news_list_item_tv_host_name);
            category = (TextView) itemView.findViewById(R.id.news_list_item_tv_category);
            star = (ImageView) itemView.findViewById(R.id.news_list_item_iv_star);

            itemView.setOnClickListener(ViewHolder.this);
            host.setOnClickListener(ViewHolder.this);
            star.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v)
        {
            if (newsListClickListener != null)
            {
                if (v.getId() == R.id.news_list_item_tv_host_name)
                {
                    newsListClickListener.onLinkClick(data.get(getAdapterPosition()).getHostname());
                    return;
                } else if (v.getId() == R.id.news_list_item_iv_star)
                {
                    star.setImageDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_star_red_24dp));
                    newsListClickListener.onNewsItemStarred(data.get(getAdapterPosition()).getId());
                    return;
                }
                newsListClickListener.onClick(getAdapterPosition(), data.get(getAdapterPosition()));
            }
        }
    }

    public interface NewsListClickListener
    {
        void onClick(int position, NewsItem newsItem);

        void onLinkClick(String hostname);

        void onNewsItemStarred(long id);
    }
}
