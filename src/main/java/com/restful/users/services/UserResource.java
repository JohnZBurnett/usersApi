package com.restful.users.services;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
	private String queryToInsertUser = "INSERT INTO users(firstName, lastName) VALUES(?, ?)";
	private String queryToGetSpecificUser = "SELECT * FROM users WHERE id = ?"; 
	public UserResource() {
		
	}
	
	private static Connection getConnection() throws URISyntaxException, SQLException, NullPointerException {
	    URI dbUri = new URI(System.getenv("DATABASE_URL"));
	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

	    return DriverManager.getConnection(dbUrl, username, password);
	}
	
	@GET
	public Response getAllUsers() {
		return Response.ok().build(); 
		
	}
	
	public ResultSet getUserFromDb(int userId) {
		ResultSet user = null; 
		
		try (Connection connection = getConnection()) {
			PreparedStatement pst = connection.prepareStatement(queryToGetSpecificUser);
			pst.setInt(1,  userId);
			user = pst.executeQuery();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return user; 
	}
	public void addUserToDb(User user) {
		try (Connection connection = getConnection()) {
			PreparedStatement pst = connection.prepareStatement(queryToInsertUser);
			pst.setString(1,  user.getFirstName());
			pst.setString(2,  user.getLastName());
			pst.executeUpdate(); 
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@POST
	@Consumes("application/json")
	public Response createNewUser(InputStream is) {
		ObjectMapper mapper = new ObjectMapper(); 
		try (Connection connection = getConnection()) {
			User user = mapper.readValue(is, User.class); 
			addUserToDb(user); 
			
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
	public Response getOneUser(@PathParam("id") int id) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		JSONArray queryResultsToJSON = null; 
		ResultSet queryResults = getUserFromDb(id); 
		String resultJSONString = null; 
		try {
			resultJSONString = convertToJSON(queryResults); 
			System.out.println(queryResultsToJSON);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		

		return Response.ok(resultJSONString, MediaType.APPLICATION_JSON).build(); 
	}
	
	public static String convertToJSON(ResultSet resultSet)
            throws Exception {
	   ObjectMapper mapper = new ObjectMapper(); 
       User resultAsUser = new User(); 
       resultAsUser.setId(resultSet.getInt("id"));
       resultAsUser.setFirstName(resultSet.getString("firstName"));
       resultAsUser.setLastName(resultSet.getString("lastName"));
       String userJSONinString = mapper.writeValueAsString(resultAsUser); 
       return userJSONinString;
    }
}
