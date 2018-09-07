package org.manny.rezt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 *
 * @author manny
 */
public class ReztConfiguration extends Configuration {
    private String resources = "/tmp/rezt/resources/";

    @JsonProperty
    public String getResources() {
	return resources;
    }

    @JsonProperty
    public void setResources(String resources) {
	this.resources = resources;
    }
}