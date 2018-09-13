package org.manny.rezt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Status {
    private final String description;
    private final int id;

    public Status(int id, String desc) {
	this.description = desc;
	this.id = id;
    }

    @JsonProperty
    public long getId() {
	return id;
    }

    @JsonProperty
    public String getDescription() {
	return description;
    }
}