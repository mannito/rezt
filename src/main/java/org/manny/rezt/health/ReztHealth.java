package org.manny.rezt.health;

import com.codahale.metrics.health.HealthCheck;

public class ReztHealth extends HealthCheck {
    public ReztHealth() {}

    @Override
    protected HealthCheck.Result check() throws Exception {
        return HealthCheck.Result.healthy();
    }
}