package com.tikal.lire.server.controller.data;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.tikal.lire.server.db.DbQueries;

@Path("/data/resource/{collection}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceController {

	ArangoDatabase database;
	DbQueries dbQueries;

	public ResourceController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	public List<BaseDocument> getAll(@PathParam("collection") String collection) {
		return dbQueries.getAll(collection);
	}

	@GET
	@Path("/{key}")
	public Response getResource(@PathParam("collection") String collection, @PathParam("key") String key) {
		return Response.ok(database.collection(collection).getDocument(key, BaseDocument.class)).build();
	}

	@POST
	public Response createResource(@PathParam("collection") String collection, Map<String, Object> data) {
		BaseDocument bd = new BaseDocument(data);
		return Response.ok(database.collection(collection).insertDocument(bd)).build();
	}

	@PUT
	@Path("/{key}")
	public Response updateTemplate(@PathParam("key") String key, Map<String, Object> body) {
		BaseDocument bd = new BaseDocument(body);
		bd.setKey(key);
		return Response.ok(database.collection("templates").updateDocument(key, bd)).build();
	}

	@DELETE
	@Path("/{key}")
	public Response deleteResource(@PathParam("collection") String collection, @PathParam("key") String key) {
		return Response.ok(database.collection(collection).deleteDocument(key)).build();
	}

}
