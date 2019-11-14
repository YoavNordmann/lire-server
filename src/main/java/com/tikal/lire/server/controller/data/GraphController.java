package com.tikal.lire.server.controller.data;

import java.util.Arrays;
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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Path("/data/graph")
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
	@Path("/{graphname}")
	public Response getGraph(@PathParam("graphname") String graphname) {
		ArangoGraph graph = database.graph(graphname);
		JsonArray resources = new JsonArray();
		graph.getVertexCollections().stream().forEach(s -> resources.add(s));
		return Response.ok(new JsonObject().put("name", graph.getInfo().getName()).put("resources", resources)).build();
	}

	@GET
	@Path("/{graphname}/edges")
	public List<BaseDocument> getEdges(@PathParam("graphname") String graphname) {
		return database.graph(graphname).getEdgeDefinitions().stream().flatMap(s -> dbQueries.getAll(s).stream())
				.collect(Collectors.toList());
	}

	@GET
	@Path("/{graphname}/vertex")
	public List<BaseDocument> getVertex(@PathParam("graphname") String graphname) {
		return database.graph(graphname).getVertexCollections().stream().flatMap(s -> dbQueries.getAll(s).stream())
				.collect(Collectors.toList());
	}

	@POST
	public Response createGraph(Map<String, Object> data) {

		String name = data.get("name").toString();
		String collectionName = name.concat("-edges");

		String[] resources = ((List<String>) data.get("resources")).toArray(new String[0]);

		EdgeDefinition edgeDefinition = new EdgeDefinition().collection(collectionName).from(resources).to(resources);

		return Response.ok(database.createGraph(name, Arrays.asList(edgeDefinition))).build();
	}

	@DELETE
	@Path("/{graphname}")
	public Response deleteGraph(@PathParam("graphname") String graphname) {
		database.graph(graphname).getEdgeDefinitions().stream().forEach(s -> database.collection(s).drop());
		database.graph(graphname).drop();
		return Response.ok().build();
	}

}
