package com.tikal.lire.server.controller;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestController {

	private static final Logger logger = LoggerFactory.getLogger(TestController.class);


	@POST
	public Response createNewTemplate(Map<String, Object> body) {
		logger.info("Recived request: {}", body);
		return Response.ok().build();
	}
	


}
