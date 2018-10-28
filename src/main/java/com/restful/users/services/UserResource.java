package com.restful.users.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
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
	public Response createNewUser(InputStream is) {
		ObjectMapper mapper = new ObjectMapper(); 
		try {
			User user = mapper.readValue(is, User.class); 
			System.out.println(user);
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user); 
			System.out.println(jsonString);
		} catch (JsonParseException e) { e.printStackTrace();}
		  catch (JsonMappingException e) {e.printStackTrace();}
		  catch (IOException e) { e.printStackTrace(); }
		
		return Response.ok().build(); 
	}
	
	@GET
	@Path("{id}")
	public Response getOneUser(@PathParam("id") int id) {
		final User user = userDB.get(id); 
		
		if (user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND); 
		}
		return Response.ok().build(); 
	}
}
