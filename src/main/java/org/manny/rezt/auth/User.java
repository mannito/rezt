package org.manny.rezt.auth;

import java.security.Principal;

public class User implements Principal {
    private final String name, id;

    public User(String id, String name) {
	this.id = id;
	this.name = name;
    }
    
    public String getId() {
	return id;
    }

    @Override
    public String getName() {
	return name;
    }
}
