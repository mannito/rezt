package org.manny.rezt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 *
 * @author manny
 */
public class ReztConfiguration extends Configuration {
    // defaults
    private String resources = "/tmp/rezt/resources/";
    private String signkey = "SECRET-SIGNKEY-REZT";

    @JsonProperty
    public String getSignkey() {
	return signkey;
    }
    
    @JsonProperty
    public void setSignkey(String signkey) {
	this.signkey = signkey;
    }

    @JsonProperty
    public String getResources() {
	return resources;
    }

    @JsonProperty
    public void setResources(String resources) {
	this.resources = resources;
    }
}