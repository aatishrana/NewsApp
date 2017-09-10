package com.example.usecase;

import com.example.Options;

/**
 * Created by Aatish on 9/10/2017.
 */

public class GitFilteredNewsOptions extends Options
{
    private boolean forceRefresh = true;

    public boolean isForceRefresh()
    {
        return forceRefresh;
    }

    public void setForceRefresh(boolean forceRefresh)
    {
        this.forceRefresh = forceRefresh;
    }
}
