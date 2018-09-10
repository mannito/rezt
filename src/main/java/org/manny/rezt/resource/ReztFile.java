package org.manny.rezt.resource;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.manny.rezt.store.ReztStore;
import org.manny.rezt.auth.User;
import org.manny.rezt.entity.ReztIndex;
import org.manny.rezt.entity.ReztObject;

@PermitAll
@Path("/")
public class ReztFile {
    private static final int COMPRESSION_LEVEL = 3;

    private final ObjectMapper jmapper = new ObjectMapper();
    @Context private SecurityContext scontext;
    private final ReztStore store;

    @Inject
    public ReztFile(ReztStore store) {
	this.store = store;
    }

    private User getUser() {
	return (User) scontext.getUserPrincipal();
    }

    private int parseLevel(String level) {
	if (level.isEmpty())
	    return COMPRESSION_LEVEL;

	return Integer.parseInt(level.substring(1));
    }

    private StreamingOutput jsonStreamResponse(Set<ReztObject> rObjectSet) {

	return (OutputStream os) -> {
		JsonGenerator generator = jmapper.getFactory().createGenerator(os);

		generator.writeStartArray();
		for (ReztObject obj: rObjectSet) {
		    generator.writeStartObject();
		    generator.writeFieldName("index");
		    generator.writeObject(obj.getIndex());
		    generator.writeFieldName("content");
		    generator.writeBinary(obj.getContent(), -1);
		    generator.writeEndObject();
		}
		generator.writeEndArray();
		generator.flush();
	};
    }

    @Timed
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Path("/up{level: (/[0-9])?}")
    public Response upload(@NotNull @Valid @FormDataParam("blob") FormDataBodyPart bodyPart,
				    @PathParam("level") String level_,
				    @QueryParam("name") String name,
				    @QueryParam("tag") Set<String> tags) {

	int level = parseLevel(level_);

	try {

	    InputStream is = bodyPart.getEntityAs(InputStream.class);
	    ReztIndex index = store.create(getUser().getId(), is, Optional.ofNullable(name), tags);
	    return Response.ok(index).build();
	} catch (IOException | NoSuchAlgorithmException ex) {

	    return Response.status(500).entity(
		    "Internal ERROR: " + ex.getMessage() + "\r\n").build();
	}
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM})
    @Path("/dl/id/{fid: [a-fA-F0-9]+}{out: (/(a|d|h))?}")
    public Response downloadFileId(@PathParam("out") Optional<String> out,
			    @NotNull @Valid @PathParam("fid") String fid) {

	try {

	    if (out.orElse("/a").equals("/h"))
		return Response.ok(store.loadIndex(getUser().getId(), fid)).build();

	    ReztObject ro = store.loadBySha1(getUser().getId(), fid);

	    if (out.orElse("/a").equals("/d"))
		return Response.ok(ro.getContent(),
			MediaType.APPLICATION_OCTET_STREAM).build();

	    return Response.ok(jsonStreamResponse(ImmutableSet.of(ro))).build();
	} catch (IOException ex) {

	    if (ex instanceof FileNotFoundException)
		return Response.status(Status.NOT_FOUND).entity(
		    "File not found: " + ex.getMessage() + "\r\n").build();

	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
		    "Internal ERROR: " + ex.getMessage() + "\r\n").build();
	}
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dl/name/{name}{out: (/(a|d|h))?}")
    public Response downloadName(@PathParam("out") String out,
				@NotNull @Valid @PathParam("name") String name,
				@QueryParam("tag") Set<String> tags) {

	try {

	    Set<ReztObject> ros = store.loadByName(getUser().getId(), name, tags);
	    return Response.ok(jsonStreamResponse(ros)).build();
	} catch (IOException ex) {

	    if (ex instanceof FileNotFoundException)
		return Response.status(Status.NOT_FOUND).entity(
		    "File not found: " + ex.getMessage() + "\r\n").build();

	    return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
		    "Internal ERROR: " + ex.getMessage() + "\r\n").build();
	}
    }
}
