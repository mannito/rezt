package org.manny.rezt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author javier
 */
public class ReztIndex {
    private final long size, birth, contentId;
    private final String id;
    private final int parts;

    public ReztIndex(String id, long size, int parts, long birth, long contentId) {
	this.id = id;
	this.size = size;
	this.parts = parts;
	this.birth = birth;
	this.contentId = contentId;
    }

    @JsonProperty
    public long getContentId() {
	return contentId;
    }

    @JsonProperty
    public long getBirth() {
	return birth;
    }

    @JsonProperty
    public String getId() {
	return id;
    }

    @JsonProperty
    public int getParts() {
	return parts;
    }

    @JsonProperty
    public long getSize() {
	return size;
    }
}