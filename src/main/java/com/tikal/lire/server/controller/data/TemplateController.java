package com.tikal.lire.server.controller.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.tikal.lire.server.db.DbQueries;
import com.tikal.lire.server.model.types.TemplateType;

@Path("/data/template")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TemplateController {

	private static String COLLECTION_NAME = "templates";

	ArangoDatabase database;
	DbQueries dbQueries;

	public TemplateController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	public List<BaseDocument> getAllTemplates(@QueryParam("type") String type) {
		Stream<BaseDocument> queryStream = dbQueries.getAll(COLLECTION_NAME).stream();
		
		Stream<BaseDocument> stream = Optional.ofNullable(type).map(t -> {
			return queryStream.filter(bd -> {
				return bd.getProperties().get("type").toString().equals(type);
			});
		}).orElseGet(() -> queryStream.filter(bd -> {
			return !bd.getProperties().get("type").toString().equals("system");
		}));
		
		return stream.collect(Collectors.toList());
	}

	@GET
	@Path("/{key}")
	public Response getTemplate(@PathParam("key") String key) {
		return Response.ok(database.collection(COLLECTION_NAME).getDocument(key, BaseDocument.class)).build();
	}

	@POST
	public Response createNewTemplate(Map<String, Object> body) {
		TemplateType type = TemplateType.valueOf(body.get("type").toString());

		switch (type) {
		case resource:
			return createResourceTemplate(body);
		case action:
			return createActionTemplate(body);
		default:
			return Response.status(Status.BAD_REQUEST).build();
		}

	}

	@PUT
	@Path("/{key}")
	public Response updateTemplate(@PathParam("key") String key, Map<String, Object> body) {
		BaseDocument bd = new BaseDocument(body);
		bd.setKey(key);
		return Response.ok(database.collection(COLLECTION_NAME).updateDocument(key, bd)).build();
	}

	@DELETE
	@Path("/{key}")
	public Response deleteTemplate(@PathParam("key") String name) {
		if (database.collection(name).exists()) {
			database.collection(name).drop();
		}
		return Response.ok(database.collection(COLLECTION_NAME).deleteDocument(name)).build();
	}

	private Response createResourceTemplate(Map<String, Object> body) {
		String name = body.get("name").toString();
		if (!database.collection(name).exists()) {
			database.createCollection(name);
			BaseDocument bd = new BaseDocument(body);
			bd.setKey(name.replace(" ", "_"));
			return Response.ok(database.collection(COLLECTION_NAME).insertDocument(bd)).build();
		} else {
			return Response
					.status(Response.Status.CONFLICT.getStatusCode(), "A template with such a name already exists")
					.build();
		}
	}

	private Response createActionTemplate(Map<String, Object> body) {
		String name = body.get("name").toString();
		BaseDocument bd = new BaseDocument(body);
		bd.setKey(name.replace(" ", "_"));
		return Response.ok(database.collection(COLLECTION_NAME).insertDocument(bd)).build();

	}

}
