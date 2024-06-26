package com.bottlerocket.repositories;

/**
 * This interface uses the Repository pattern from Domain Driven Design.
 * A repository encapsulates the set of objects persisted in a data store and the operations performed over them.
 * More on this pattern here: https://martinfowler.com/eaaCatalog/repository.html
 */
public interface Repository {
    void init(String connectionString, String database);
    String readOne(String table, String filter, String columnName);
}
