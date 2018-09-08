package org.manny.rezt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author javier
 */
public class ReztIndex {
    private String name, id;
    private long size, parts;

    public ReztIndex(String name, String id, long size, long parts) {
	this.name = name;
	this.id = id;
	this.size = size;
	this.parts = parts;
    }

    @JsonProperty
    public String getId() {
	return id;
    }

    @JsonProperty
    public String getName() {
	return name;
    }

    @JsonProperty
    public long getParts() {
	return parts;
    }

    @JsonProperty
    public long getSize() {
	return size;
    }
}