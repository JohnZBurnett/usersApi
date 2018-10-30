package com.restful.users.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.restful.users.domain.User;

public class UserDatabaseHelper {
	private String queryToInsertUser = "INSERT INTO users(firstName, lastName) VALUES(?, ?) RETURNING id";
	private String queryToGetSpecificUser = "SELECT * FROM users WHERE id = ?"; 
	private String queryToGetAllUsers = "SELECT * FROM users";

	public UserDatabaseHelper() {
		
	}
	
	public static Connection getConnection() throws URISyntaxException, SQLException, NullPointerException {
	    URI dbUri = new URI(System.getenv("DATABASE_URL"));
	    String username = dbUri.getUserInfo().split(":")[0];
	    String password = dbUri.getUserInfo().split(":")[1];
	    String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

	    return DriverManager.getConnection(dbUrl, username, password);
	}
	
	public User addUserToDb(User user) {
		try (Connection connection = getConnection()) {
			PreparedStatement pst = connection.prepareStatement(queryToInsertUser);
			pst.setString(1,  user.getFirstName());
			pst.setString(2,  user.getLastName());
			pst.execute();
			ResultSet lastUpdatedUserResults = pst.getResultSet();
			if(lastUpdatedUserResults.next()) {
			   int lastUpdatedUserId = lastUpdatedUserResults.getInt(1);
			   user.setId(lastUpdatedUserId);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return user; 
	}
	
	public HashMap getAllUsersFromDb() throws NullPointerException, SQLException, URISyntaxException {
		HashMap<String, ArrayList<User>> resultsMap = new HashMap<String, ArrayList<User>>(); 
		ArrayList userArray = new ArrayList<User>();  
		PreparedStatement pst = getConnection().prepareStatement(queryToGetAllUsers);
		ResultSet queryResults = pst.executeQuery(); 
		while (queryResults.next()) {
			User resultAsUser = new User();
		    resultAsUser.setId(queryResults.getInt("id"));
		    resultAsUser.setFirstName(queryResults.getString("firstName"));
		    resultAsUser.setLastName(queryResults.getString("lastName"));
		    userArray.add(resultAsUser); 
		}
		
		resultsMap.put("users", userArray); 
		return resultsMap; 
		
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
	
	
}
