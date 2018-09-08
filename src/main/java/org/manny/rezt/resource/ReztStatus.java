package org.manny.rezt.resource;

import com.codahale.metrics.annotation.Timed;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.manny.rezt.entity.Status;

@PermitAll // XXX: Role ADMIN
@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
public class ReztStatus {
    private final String defaultResource;
    private final AtomicLong counter;

    public ReztStatus(String defaultResource) {
        this.defaultResource = defaultResource;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Status getStatus(@QueryParam("resource") Optional<String> resource) {
	final String code;

	if (!new File(resource.orElse(defaultResource)).isDirectory())
	    code = "ERR";
	else code = "OK";    

        return new Status(counter.incrementAndGet(), code);
    }
}