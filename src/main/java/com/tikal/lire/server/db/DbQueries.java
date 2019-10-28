package com.tikal.lire.server.db;

import java.util.Collections;
import java.util.List;

import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.util.MapBuilder;

public class DbQueries {

	private final String searchByNameQuery = "FOR t IN @@collection FILTER t.name == @name RETURN t";
	private final String getAllQuery = "FOR t IN @@collection RETURN t";
	private ArangoDatabase db;

	public DbQueries(ArangoDatabase db) {
		this.db = db;
	}

	public List<BaseDocument> searchByName(String collection, String name) {
		try {
			return db.query(searchByNameQuery, 
					new MapBuilder().put("name", name).put("@collection", collection).get(),
					null,
					BaseDocument.class).asListRemaining();
		} catch (ArangoDBException e) {
			System.err.println("Failed to execute query. " + e.getMessage());
			return Collections.emptyList();
		}
	}

	public List<BaseDocument> getAll(String collection) {
		return db.query(getAllQuery, 
				new MapBuilder().put("@collection", collection).get(),
				null, BaseDocument.class).asListRemaining();
	}

}
