package com.bk.bkskup3.repo.serialization.wire;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by IntelliJ IDEA.
 * User: SG0891787
 * Date: 10/8/12
 * Time: 12:34 PM
 */
public class CursorDescriptor
{
    private int count;
    private int minFetch;
    private int maxFetch;

    @JsonCreator
    CursorDescriptor(@JsonProperty("count") int count,
                     @JsonProperty("minFetch")int minFetch,
                     @JsonProperty("maxFetch")int maxFetch)
    {
        this.count = count;
        this.minFetch = minFetch;
        this.maxFetch = maxFetch;
    }

    public int getCount()
    {
        return count;
    }

    public int getMinFetch()
    {
        return minFetch;
    }

    public int getMaxFetch()
    {
        return maxFetch;
    }
}

