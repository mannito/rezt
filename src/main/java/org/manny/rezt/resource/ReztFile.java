package org.manny.rezt.resource;

import com.codahale.metrics.annotation.Timed;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.manny.rezt.auth.User;

@PermitAll
@Path("/")
public class ReztFile {
    private static final int COMPRESSION_LEVEL = 3;
    @Context private SecurityContext scontext;

    private User getUser() {
	return (User) scontext.getUserPrincipal();
    }

    private int parseLevel(String level) {
	if (level.isEmpty())
	    return COMPRESSION_LEVEL;
	
	return Integer.parseInt(level.substring(1));
    }

    @Timed
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/up{level: (/[0-9])?}")
    public Response upload(@NotNull @Valid @FormDataParam("blob") FormDataBodyPart bodyPart,
				    @PathParam("level") String level_) {
	int level = parseLevel(level_);
	User user = getUser();

	try {
	    InputStream is = bodyPart.getEntityAs(InputStream.class);
	    is.available();
	} catch (IOException ioe) {
	    return Response
		    .status(500)
		    .entity("I/O ERROR: " + ioe.getMessage() + "\r\n")
		    .build();
	}

	String sha1 = "ab1234fe"; // = persistence.create(name)
	return Response.ok("uploaded: " + sha1 + "/" + level,
			    MediaType.TEXT_PLAIN_TYPE).build();
    }

    @GET
    @Path("/dl/{fid: [a-fA-F0-9]+}{jsopt: (/(i|d))?}")
    public Response download(@PathParam("jsopt") String jsopt,
				 @NotNull @Valid @PathParam("fid") String fid) {
	User user = getUser();
	return Response.ok(String.format("{ download: %s, u: %s}",
			   fid, user.getId()), MediaType.TEXT_PLAIN_TYPE).build();
    }
}