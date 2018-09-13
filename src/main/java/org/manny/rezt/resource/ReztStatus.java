package org.manny.rezt.resource;

import com.codahale.metrics.annotation.Timed;
import java.io.IOException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.manny.rezt.store.ReztStore;
import org.manny.rezt.entity.Status;

@Path("/status")
@Produces(MediaType.APPLICATION_JSON)
public class ReztStatus {
    private final ReztStore store;

    @Inject
    public ReztStatus(ReztStore store) {
        this.store = store;
    }

    @GET
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {

	try {
	    return Response.ok(new Status(0, store.status())).build();
	} catch (IOException ioe) {
	    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
		    .entity(new Status(1, "Internal error: " +
				ioe.getMessage())).build();
	}
    }
}