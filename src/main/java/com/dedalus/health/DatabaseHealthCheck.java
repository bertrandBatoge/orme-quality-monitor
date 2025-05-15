package com.dedalus.health;

import com.dedalus.repository.JenkinsJobRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@ApplicationScoped
@Readiness
public class DatabaseHealthCheck implements HealthCheck {
    private final JenkinsJobRepository repository;

    @Inject
    public DatabaseHealthCheck(JenkinsJobRepository repository) {
        this.repository = repository;
    }

    @Override
    public HealthCheckResponse call() {
        try {
            // Try to count the number of jobs to check database connectivity
            long count = repository.count();
            return HealthCheckResponse.up("Database connection is working. Jobs count: " + count);
        } catch (Exception e) {
            return HealthCheckResponse.down("Database connection failed: " + e.getMessage());
        }
    }
}
