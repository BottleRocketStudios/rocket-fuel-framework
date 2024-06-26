package com.bottlerocket.repositories;

import org.apache.commons.lang3.NotImplementedException;

/**
 * MySQL Repo impl
 *
 * Created by ford.arnett on 6/15/20
 */
public class MySQLRepository implements Repository {
    @Override
    public void init(String connectionString, String database) {
        throw new NotImplementedException("not yet implemented");
    }

    @Override
    public String readOne(String table, String filter, String columnName) {
        throw new NotImplementedException("not yet implemented");
    }
}
