package org.manny.rezt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author javier
 */
public class ReztObject {
    private final ReztIndex index;
    private final byte content[];

    public ReztObject(ReztIndex index, byte[] content) {
	this.index = index;
	this.content = content;
    }

    @JsonProperty(required = true)
    public ReztIndex getIndex() {
	return index;
    }

    @JsonProperty(required = true)    
    public byte[] getContent() {
	return content;
    }
}