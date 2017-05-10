package com.philips.websuite.backend.dao;

import com.google.inject.Inject;
import com.philips.app.data.DataQuery;

/**
 * @author crhobus
 */
public abstract class AbstractDAO {

    @Inject
    private DataQuery query;

    public DataQuery getQuery() {
        return query;
    }

    public void setQuery(DataQuery query) {
        this.query = query;
    }
}
