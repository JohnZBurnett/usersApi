package com.restful.users.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
	public Response createNewUser() {
		return Response.ok().build(); 
	}
}
