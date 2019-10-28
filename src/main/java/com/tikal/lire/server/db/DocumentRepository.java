package com.tikal.lire.server.db;

import java.util.Collection;

import com.arangodb.ArangoCollection;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.DocumentCreateEntity;
import com.arangodb.entity.DocumentDeleteEntity;
import com.arangodb.entity.DocumentUpdateEntity;
import com.arangodb.entity.IndexEntity;

public class DocumentRepository {

	private ArangoCollection collection;

	public DocumentRepository(ArangoCollection collection) {
		this.collection = collection;
	}
	
	public String getCollectionName() {
		return collection.name();
	}

	public DocumentCreateEntity<BaseDocument> insert(BaseDocument value) {
		return collection.insertDocument(value);
	}

	public DocumentDeleteEntity<Void> delete(String key) {
		return collection.deleteDocument(key);
	}

	public BaseDocument get(String key) {
		return collection.getDocument(key, BaseDocument.class, null);
	}

	public Collection<IndexEntity> getAll() {
		return collection.getIndexes();
	}

	public DocumentUpdateEntity<BaseDocument> update(String key, BaseDocument value) {
		return collection.updateDocument(key, value);
	}



}
