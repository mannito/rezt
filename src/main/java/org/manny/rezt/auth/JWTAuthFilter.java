package org.manny.rezt.auth;

import io.dropwizard.auth.AuthenticationException;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author javier
 */
@Provider
@PreMatching
public class JWTAuthFilter implements ContainerRequestFilter {
    private static final Pattern BEARER_PATTERN = Pattern.compile("^(Bearer )(.*)$");
    private final JWTAuthenticator authenticator;

    public JWTAuthFilter(JWTAuthenticator authenticator) {
	this.authenticator = authenticator;
    }

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
	String header = crc.getHeaderString(HttpHeaders.AUTHORIZATION);

	if (header == null)
	    throw new WebApplicationException("Invalid Authorization", Status.UNAUTHORIZED);

	Matcher matcher = BEARER_PATTERN.matcher(header);

	if (!matcher.matches())
	    throw new WebApplicationException("Invalid Bearer: <" + header + ">", Status.UNAUTHORIZED);

	try {
	    String jws = matcher.group(2);
	    Optional<User> creds = authenticator.authenticate(jws);
	    SecurityContext scontext = crc.getSecurityContext();
	    crc.setSecurityContext(new SecurityContext() {
		@Override
		public Principal getUserPrincipal() { return creds.get(); }

		@Override
		public boolean isUserInRole(String string) { return true; }

		@Override
		public boolean isSecure() { return scontext.isSecure(); }

		@Override
		public String getAuthenticationScheme() { return "Bearer"; }
	    });
	} catch (AuthenticationException ae) {
	    throw new WebApplicationException(
		    "Invalid Token: <" + ae.getMessage() + ">", Status.UNAUTHORIZED
	    );
	}
    }
}