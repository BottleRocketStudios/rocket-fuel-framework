package com.bottlerocket.repositories;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongodbRepository implements Repository {
    private MongoDatabase db;
    private String connectionString;
    private String database;

    public MongodbRepository(String connectionString, String database) {
        this.connectionString = connectionString;
        this.database = database;
    }

    public void init(String connectionString, String database) {
        this.connectionString = connectionString;
        this.database = database;
    }

    public String readOne(String table, String jsonFilter, String columnName) {
        // lazy load connection to database to avoid unnecessary connections
        if (db == null) {
            MongoClient mongoClient = new MongoClient(new MongoClientURI(connectionString));
            db = mongoClient.getDatabase(database);
        }

        BasicDBObject filter = BasicDBObject.parse(jsonFilter);
        FindIterable<Document> documents = db.getCollection(table).find(filter);

        if (documents.first() == null) {
            return "";
        }

        Object data = documents.first().get(columnName);

        return data instanceof Document ? ((Document) data).toJson() : data.toString();
    }
}
