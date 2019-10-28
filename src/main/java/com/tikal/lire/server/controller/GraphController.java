package com.tikal.lire.server.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoGraph;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.EdgeDefinition;
import com.tikal.lire.server.db.DbQueries;

@Path("/graphs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphController {

	ArangoDatabase database;
	DbQueries dbQueries;

	public GraphController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	
	@GET
	@Path("/{key}")
	public ArangoGraph getGraph(@PathParam("key") String key) {
		return database.graph(key);
	}
	

	@GET
	@Path("/{key}/edges")
	public List<BaseDocument> getEdges(@PathParam("key") String key) {
		return database.graph(key).getEdgeDefinitions().stream()
				.flatMap(s -> dbQueries.getAll(s).stream()).collect(Collectors.toList());
	}

	
	@GET
	@Path("/{key}/vertex")
	public List<BaseDocument> getVertex(@PathParam("key") String key) {
		return database.graph(key).getVertexCollections().stream()
				.flatMap(s -> dbQueries.getAll(s).stream()).collect(Collectors.toList());
	}
	

	@POST
	public Response createGraph(Map<String, Object> data) {

		String name = data.get("name").toString();
		String collectionName = name.concat("-edges");

		List<String> from = (List<String>) ((Map<String, Object>) data.get("collections")).get("from");
		List<String> to = (List<String>) ((Map<String, Object>) data.get("collections")).get("to");

		EdgeDefinition edgeDefinition = new EdgeDefinition().collection(collectionName)
				.from(from.toArray(new String[from.size()])).to(to.toArray(new String[to.size()]));

		return Response.ok(database.createGraph(name, Arrays.asList(edgeDefinition))).build();
	}
	

	@DELETE
	@Path("/{key}")
	public Response deleteGraph(@PathParam("key") String key) {
		database.graph(key).getEdgeDefinitions().stream().forEach(s -> database.collection(s).drop());
		database.graph(key).drop();
		return Response.ok().build();
	}

}
