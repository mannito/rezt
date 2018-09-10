package org.manny.rezt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author javier
 */
public class ReztObject {
    private final ReztIndex index;
    private final InputStream stream;

    public ReztObject(ReztIndex index, InputStream stream) {
	this.index = index;
	this.stream = stream;
    }

    @JsonProperty(required = true)
    public ReztIndex getIndex() {
	return index;
    }

    @JsonIgnore
    public InputStream getContent() throws IOException {
	return stream;
    }
}