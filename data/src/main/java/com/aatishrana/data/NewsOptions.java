package com.aatishrana.data;


/**
 * Created by Aatish on 9/10/2017.
 */

public class NewsOptions
{
    private boolean forceRefresh = true;

    private boolean filter = false;
    private String filterKey, filterValue;

    private boolean sort = false;
    private String sortKey;

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh)
    {
        this.forceRefresh = forceRefresh;
    }

    public boolean isFilter()
    {
        return filter;
    }

    public void setFilter(boolean filter)
    {
        this.filter = filter;
        setForceRefresh(false);
    }

    public String getFilterKey()
    {
        return filterKey;
    }

    public void setFilterKey(String filterKey)
    {
        this.filterKey = filterKey;
    }

    public String getFilterValue()
    {
        return filterValue;
    }

    public void setFilterValue(String filterValue)
    {
        this.filterValue = filterValue;
    }

    public boolean isSort()
    {
        return sort;
    }

    public void setSort(boolean sort)
    {
        this.sort = sort;
        setForceRefresh(false);
    }

    public String getSortKey()
    {
        return sortKey;
    }

    public void setSortKey(String sortKey)
    {
        this.sortKey = sortKey;
    }


    @Override
    public String toString()
    {
        return "NewsOptions{" +
                "forceRefresh=" + forceRefresh +
                ", filter=" + filter +
                ", filterKey='" + filterKey + '\'' +
                ", filterValue='" + filterValue + '\'' +
                ", sort=" + sort +
                ", sortKey='" + sortKey + '\'' +
                '}';
    }
}
