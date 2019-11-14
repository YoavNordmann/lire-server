package com.tikal.lire.server.controller.info;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.CollectionEntity;
import com.tikal.lire.server.db.DbQueries;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceInfoController {

	ArangoDatabase database;
	DbQueries dbQueries;

	public ResourceInfoController(ArangoDatabase database, DbQueries dbQueries) {
		this.database = database;
		this.dbQueries = dbQueries;
	}

	@GET
	@Path("/resources")
	public Response getResourcesInfo(@QueryParam("type") String type) {
		Stream<BaseDocument> queryStream = dbQueries.getAll("templates").stream();

		Stream<BaseDocument> stream = Optional.ofNullable(type).map(t -> {
			return queryStream.filter(bd -> {
				return bd.getProperties().get("type").toString().equals(type);
			});
		}).orElseGet(() -> queryStream.filter(bd -> {
			return !bd.getProperties().get("type").toString().equals("system");
		}));

		JsonArray array = new JsonArray();
		stream.map(infoMapper).forEach(j -> array.add(j));
		return Response.ok(array).build();
	}

	@GET
	@Path("/resources/name")
	public List<String> getResourcesNameInfo() {
		return dbQueries.getAll("templates").stream().filter(bd -> {
			return bd.getProperties().get("type").toString().equals("resource");
		}).map(bd -> bd.getAttribute("name").toString()).collect(Collectors.toList());
	}

	private Function<BaseDocument, JsonObject> infoMapper = bd -> new JsonObject().put("name", bd.getAttribute("name"))
			.put("description", bd.getAttribute("description")).put("type", bd.getAttribute("type"));;

}
