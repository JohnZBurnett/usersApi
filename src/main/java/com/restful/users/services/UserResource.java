package com.restful.users.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.restful.users.domain.User; 

@Path("/users")
public class UserResource {
	private Map<Integer, User> userDB = new ConcurrentHashMap<Integer, User>(); 
	private AtomicInteger idCounter = new AtomicInteger(); 
	
	public UserResource() {
		
	}
	
	@GET
	public Response getAllUsers() {
		return Response.ok().build(); 
		
	}
	
	@POST
	@Consumes("application/json")
	public Response createNewUser(InputStream is) {
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			User user = mapper.readValue(is, User.class); 
			user.setId( idCounter.incrementAndGet());
			System.out.println(user);
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user); 
			System.out.println(jsonString);
			userDB.put(user.getId(), user);
			return Response.ok(jsonString, MediaType.APPLICATION_JSON).build(); 
		} catch (JsonParseException e) { e.printStackTrace();}
		  catch (JsonMappingException e) {e.printStackTrace();}
		  catch (IOException e) { e.printStackTrace(); }
		
		return Response.ok().build(); 
	}
	
	@GET
	@Path("{id}")
	@Produces("application/json")
	public Response getOneUser(@PathParam("id") int id) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null; 
		try {
			final User user = userDB.get(id); 
			if (user == null) {
				throw new WebApplicationException(Response.Status.NOT_FOUND); 
			}
			jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user); 
		} catch (JsonMappingException e) {e.printStackTrace(); }
		  catch (JsonProcessingException e) {e.printStackTrace(); }
		
		return Response.ok(jsonString, MediaType.APPLICATION_JSON).build(); 
	}
}
