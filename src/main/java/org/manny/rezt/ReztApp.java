package org.manny.rezt;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.manny.rezt.auth.JWTAuthFilter;
import org.manny.rezt.auth.JWTAuthenticator;
import org.manny.rezt.health.ReztHealth;
import org.manny.rezt.resource.ReztFile;
import org.manny.rezt.resource.ReztStatus;

public class ReztApp extends Application<ReztConfiguration> {

    public static void main(String[] args) throws Exception {
	new ReztApp().run(args);
    }

    @Override
    public String getName() {
	return "rezt";
    }

    @Override
    public void initialize(Bootstrap<ReztConfiguration> bootstrap) {
	// XXX
    }

    @Override
    public void run(ReztConfiguration configuration, Environment environment) {
	final ReztStatus rstat = new ReztStatus(configuration.getResources());
	final ReztHealth rhealth = new ReztHealth();

	environment.jersey().register(new AbstractBinder() {
	    @Override
	    protected void configure() {
		bind(new ReztStore("123.456")).to(ReztStore.class);
	    }
	});
	environment.jersey().register(new AuthDynamicFeature(
		new JWTAuthFilter(new JWTAuthenticator(configuration.getSignkey()))
	));
	environment.jersey().register(rstat);
	environment.jersey().register(ReztFile.class);
	environment.jersey().register(MultiPartFeature.class);
	environment.healthChecks().register("ReztHealth", rhealth);
    }
}