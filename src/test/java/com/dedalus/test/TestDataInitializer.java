package com.dedalus.test;

import com.dedalus.model.JenkinsJob;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TestDataInitializer {

    @Inject
    EntityManager em;

    @ConfigProperty(name = "quarkus.test.profile", defaultValue = "")
    String activeProfile;

    @Transactional
    public void loadTestData(@Observes StartupEvent evt) {
        // Only initialize data for integration tests
        if (activeProfile.equals("test")) {
            // Add test data
            JenkinsJob job1 = new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                    "https://jenkins.example.com/pharma-tests");
            JenkinsJob job2 = new JenkinsJob("Presc", "v3.21.00", "presc-tests",
                    "https://jenkins.example.com/presc-tests");

            em.persist(job1);
            em.persist(job2);
        }
    }
}
