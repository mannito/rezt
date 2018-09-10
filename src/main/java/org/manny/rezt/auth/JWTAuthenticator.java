package org.manny.rezt.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import java.util.Optional;

/**
 *
 * @author javier
 */
public class JWTAuthenticator implements Authenticator<String, User> {
    private static final String ISSUER = "REZT";
    private final JWTVerifier verifier;

    public JWTAuthenticator(String signkey) {
	verifier = JWT.require(Algorithm.HMAC256(signkey)).withIssuer(ISSUER).build();
    }

    @Override
    public Optional<User> authenticate(String token) throws AuthenticationException {
	try {
	    DecodedJWT jwt = verifier.verify(token);
	    return Optional.of(new User(jwt.getClaim("id").asString(), jwt.getSubject()));
	} catch (JWTVerificationException jve) {
	    throw new AuthenticationException(
		    String.format("Invalid JWT: <%s> (%s)", token, jve.getMessage()));
	}
    }
}