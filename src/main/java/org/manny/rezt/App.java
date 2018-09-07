package org.manny.rezt;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.manny.rezt.health.ReztHealth;
import org.manny.rezt.resource.ReztStatus;

public class App extends Application<ReztConfiguration> {

    public static void main(String[] args) throws Exception {
	new App().run(args);
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

	environment.jersey().register(rstat);
	environment.jersey().register(MultiPartFeature.class);
	environment.healthChecks().register("ReztHealth", rhealth);
    }
}