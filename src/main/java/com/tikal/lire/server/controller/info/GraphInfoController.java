package com.tikal.lire.server.controller.info;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.GraphEntity;
import com.tikal.lire.server.db.DbQueries;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphInfoController {

	ArangoDatabase database;
	DbQueries dbQueries;

	public GraphInfoController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	@Path("/graphs")
	public List<String> getGraphsInfo() {
		return database.getGraphs().stream().map(GraphEntity::getName).collect(Collectors.toList());
	}

}
