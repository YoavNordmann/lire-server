package com.tikal.lire.server.controller.data;

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

@Path("/data/system")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppdataController {
	
	private static String COLLECTION_NAME = "appdata";

	ArangoDatabase database;
	DbQueries dbQueries;

	public AppdataController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	@Path("/{key}")
	public Response getSystemData(@PathParam("key") String key) {
		return Response.ok(database.collection(COLLECTION_NAME).getDocument(key, BaseDocument.class)).build();
	}

	@POST
	public Response createSystemData(Map<String, Object> data) {
		BaseDocument bd = new BaseDocument(data);
		return Response.ok(database.collection(COLLECTION_NAME).insertDocument(bd)).build();
	}
	
	@PUT
	@Path("/{key}")	
	public Response updateSystemData(@PathParam("key") String key, Map<String, Object> body) {
		BaseDocument bd = new BaseDocument(body);
		bd.setKey(key);
		return Response.ok(database.collection("templates").updateDocument(key, bd)).build();
	}
	
	
	@DELETE
	@Path("/{key}")
	public Response deleteSystemData(@PathParam("key") String key) {
		return Response.ok(database.collection(COLLECTION_NAME).deleteDocument(key)).build();
	}

}
