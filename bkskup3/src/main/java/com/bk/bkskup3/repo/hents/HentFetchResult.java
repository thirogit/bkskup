package com.bk.bkskup3.repo.hents;

import com.bk.bkskup3.repo.serialization.wire.JsonHent;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class HentFetchResult {
    private List<JsonHent> chunk;
    private long maxlastmodified;
    private long since;

    public List<JsonHent> getChunk() {
        return chunk;
    }

    public void setChunk(List<JsonHent> chunk) {
        this.chunk = chunk;
    }

    @JsonProperty("maxlastmodified")
    public long getMaxLastModified() {
        return maxlastmodified;
    }

    public void setMaxLastModified(long maxlastmodified) {
        this.maxlastmodified = maxlastmodified;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }
}
