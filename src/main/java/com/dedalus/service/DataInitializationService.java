package com.dedalus.service;

import com.dedalus.config.ApplicationConfig;
import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsJobRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

@ApplicationScoped
public class DataInitializationService {

    private static final Logger LOG = Logger.getLogger(DataInitializationService.class);

    @Inject
    JenkinsJobRepository repository;

    @Inject
    ApplicationConfig config;

    @Transactional
    public void loadInitialData(@Observes StartupEvent evt) {
        // Skip initialization if disabled in config
        if (!config.enableDataInitialization()) {
            LOG.info("Data initialization is disabled");
            return;
        }

        // Only seed data if the database is empty
        if (repository.count() > 0) {
            LOG.info("Database already contains data, skipping initialization");
            return;
        }

        LOG.info("Initializing database with sample Jenkins jobs");

        // Pharma team jobs
        repository.persist(new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                config.jenkins().baseUrl() + "/job/HORME/job/DEV_Builds/view/All/job/global_repo/job/DEV_V3.21.00/job/pharma-tests"));
        // Add other jobs as before, using config.jenkins().baseUrl()

        LOG.info("Database initialization completed");
    }
}
