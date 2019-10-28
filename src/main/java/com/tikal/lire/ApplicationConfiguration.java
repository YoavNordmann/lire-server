package com.tikal.lire;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.arangodb.ArangoDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tikal.lire.server.db.DbQueries;

@Dependent
public class ApplicationConfiguration {
	
    @Inject
    ArangoDatabase database;
    
	@Produces
	@Singleton
	public DbQueries dbQueries() {
		return new DbQueries(database);
	}

}
