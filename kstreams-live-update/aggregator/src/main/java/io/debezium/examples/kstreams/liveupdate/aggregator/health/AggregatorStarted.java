package io.debezium.examples.kstreams.liveupdate.aggregator.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import io.debezium.examples.kstreams.liveupdate.aggregator.StreamsPipelineManager;
import io.debezium.examples.kstreams.liveupdate.aggregator.cdi.Eager;

@Health
@ApplicationScoped
public class AggregatorStarted implements HealthCheck {

    @Inject @Eager
    private StreamsPipelineManager spm;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("aggregator");

        if (spm.isStarted()) {
            responseBuilder.withData("pipeline started", true)
                .up();
        }
        else {
            responseBuilder.withData("pipeline started", false)
                .down();
        }

        return responseBuilder.build();
    }
}
