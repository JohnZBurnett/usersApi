package com.restful.users.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.restful.users.domain.User; 

@Path("/users")
public class UserResource {
	private Map<Integer, User> userDB = new ConcurrentHashMap<Integer, User>(); 
	private AtomicInteger idCounter = new AtomicInteger(); 
	private String queryToInsertUser = "INSERT INTO users(id, firstName, lastName) VALUES(?, ?, ?)";
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
	
	public void addUserToDb(User user) {
		try (Connection connection = getConnection()) {
			PreparedStatement pst = connection.prepareStatement(queryToInsertUser);
			pst.setInt(1,  user.getId());
			pst.setString(2,  user.getFirstName());
			pst.setString(3,  user.getLastName());
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
	@Path("/dbtest")
	public Response testPostToDb() {
		try (Connection connection = getConnection()) {
			String query = "INSERT INTO users(id, firstName, lastName) VALUES(?, ?, ?)";
			int id = 9999; 
			String firstName = "james";
			String lastName = "test";
    		PreparedStatement pst = connection.prepareStatement(query);
    		pst.setInt(1,  id);
    		pst.setString(2, firstName);
    		pst.setString(3, lastName);
    		pst.executeUpdate(); 
    		Statement st = connection.createStatement(); 
    		ResultSet rs = st.executeQuery("SELECT VERSION()"); 
    		if (rs.next()) {
    			System.out.println(rs.getString(1));
    		}
    	} catch (SQLException e) {
    		System.out.println("Connection failure.:");
    		e.printStackTrace(); 
    	} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return Response.ok().build(); 
	}
	@POST
	@Consumes("application/json")
	public Response createNewUser(InputStream is) {
		ObjectMapper mapper = new ObjectMapper(); 
		try (Connection connection = getConnection()) {
			User user = mapper.readValue(is, User.class); 
			user.setId( idCounter.incrementAndGet());
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
