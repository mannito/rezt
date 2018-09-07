package org.manny.rezt.resource;

import com.codahale.metrics.annotation.Timed;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.manny.rezt.entity.Status;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
public class ReztStatus {
    private final String defaultCode;
    private final AtomicLong counter;

    public ReztStatus(String defaultCode) {
        this.defaultCode = defaultCode;
        this.counter = new AtomicLong();
    }

    @GET
    @Timed
    public Status getStatus(@QueryParam("resource") Optional<String> resource) {
	final String code;
	if (new File(resource.orElse(defaultCode)).isDirectory())
	    code = "OK";
	else code = "ERR";    

        return new Status(counter.incrementAndGet(), code);
    }
}