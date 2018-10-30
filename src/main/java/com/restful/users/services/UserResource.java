package com.restful.users.services;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement; 

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.restful.users.domain.User; 

@Path("/users")
public class UserResource {
	private Map<Integer, User> userDB = new ConcurrentHashMap<Integer, User>(); 
	private AtomicInteger idCounter = new AtomicInteger(); 
	private String queryToInsertUser = "INSERT INTO users(firstName, lastName) VALUES(?, ?) RETURNING id";
	private String queryToGetSpecificUser = "SELECT * FROM users WHERE id = ?"; 
	private String queryToGetAllUsers = "SELECT * FROM users";
	private UserDatabaseHelper databaseHelper = new UserDatabaseHelper(); 
	public UserResource() {
		
	}
	
	@GET
	public Response getAllUsers() throws JsonProcessingException, NullPointerException, SQLException, URISyntaxException {
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, ArrayList<User>> resultsMap = databaseHelper.getAllUsersFromDb();
		String userJSONinString = mapper.writeValueAsString(resultsMap);
		return Response.ok(userJSONinString).build(); 
		
	}
	
	@POST
	@Consumes("application/json")
	public Response createNewUser(InputStream is) {
		ObjectMapper mapper = new ObjectMapper(); 
		try (Connection connection = databaseHelper.getConnection()) {
			User user = mapper.readValue(is, User.class); 
			user = databaseHelper.addUserToDb(user); 
			String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
			return Response.ok(jsonString, MediaType.APPLICATION_JSON).build(); 
		} catch (JsonParseException e) { e.printStackTrace();}
		  catch (JsonMappingException e) {e.printStackTrace();}
		  catch (IOException e) { e.printStackTrace(); }
		  catch (SQLException e) {
			  e.printStackTrace(); 
		  }
		  catch (URISyntaxException e) {
			  e.printStackTrace(); 
		  }
		
		return Response.ok().build(); 
	}
	
	@GET
	@Path("{id}")
	@Produces("application/json")
	public Response getOneUser(@PathParam("id") int id) throws JsonProcessingException, SQLException {
		ObjectMapper mapper = new ObjectMapper();
		JSONArray queryResultsToJSON = null; 
		ResultSet queryResults = databaseHelper.getUserFromDb(id); 
		String resultJSONString = null; 
		if (queryResults.next() == false) {
			return Response.status(404).entity("No user exists for the specified ID.").build(); 
		}
		
		try {
			resultJSONString = convertToJSON(queryResults); 
			System.out.println("RESULTS AS JSON");
			System.out.println(resultJSONString);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		
		return Response.ok(resultJSONString, MediaType.APPLICATION_JSON).build(); 
	}
	
	public static String convertToJSON(ResultSet resultSet)
            throws Exception {
	   ObjectMapper mapper = new ObjectMapper(); 
       User resultAsUser = new User(); 
       resultSet.next(); 
       resultAsUser.setId(resultSet.getInt("id"));
       resultAsUser.setFirstName(resultSet.getString("firstName"));
       resultAsUser.setLastName(resultSet.getString("lastName"));
       String userJSONinString = mapper.writeValueAsString(resultAsUser); 
       System.out.println(userJSONinString);
       return userJSONinString;
    }
}
