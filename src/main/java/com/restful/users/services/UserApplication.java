package com.restful.users.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/v1/")
public class UserApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> empty = new HashSet<Class<?>>();
	
	public UserApplication() {
		singletons.add(new UserResource()); 
	}
	
	@Override 
	public Set<Class<?>> getClasses() {
		return empty; 
	}
	
	@Override 
	public Set<Object> getSingletons() {
		return singletons; 
	}
}
