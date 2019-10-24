package com.tikal.lr.server.controller;

import java.util.List;
import java.util.Map;

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
import com.arangodb.entity.BaseDocument;
import com.tikal.lr.server.db.DbQueries;

@Path("/templates")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateController {

	ArangoDatabase database;
	DbQueries dbQueries;
	

	public TemplateController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	public List<BaseDocument> getAllTemplates() {
		return dbQueries.getAll("templates");
	}

	@GET
	@Path("/{key}")
	public Response getTemplate(@PathParam("key") String key) {
		return Response.ok(database.collection("templates").getDocument(key, BaseDocument.class)).build();
	}

	@POST
	public Response createNewTemplate(Map<String, Object> body) {
		String name = body.get("name").toString();
		if(!database.collection(name).exists()) {
			database.createCollection(name);
			BaseDocument bd = new BaseDocument(body);
			bd.setKey(name);
			return Response.ok(database.collection("templates").insertDocument(bd)).build();			
		}else {
			return Response.status(Response.Status.CONFLICT.getStatusCode(), "A template with such a name already exists").build();
		}
	}
	
	@DELETE
	@Path("/{key}")
	public Response deleteTemplate(@PathParam("key") String name) {
		database.collection(name).drop();
		return Response.ok(database.collection("templates").deleteDocument(name)).build();
	}

}
