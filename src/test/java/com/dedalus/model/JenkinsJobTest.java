package com.dedalus.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class JenkinsJobTest {

    @Inject
    EntityManager em;

    @Test
    @Transactional
    void testPersistAndRetrieveJenkinsJob() {
        String uniqueSuffix = System.currentTimeMillis() + "";

        // Create a new JenkinsJob
        JenkinsJob job = new JenkinsJob(
                "Pharma" + uniqueSuffix,
                "v3.21.00",
                "pharma-tests" + uniqueSuffix,
                "https://ci-jenkins.orbis.dedalus.com/job/HORME/job/DEV_Builds/view/All/job/global_repo/job/DEV_V3.21.00/job/pharma-tests");

        // Persist it
        em.persist(job);
        em.flush();

        // Clear the persistence context to ensure we're getting from the database
        em.clear();

        // Retrieve it
        JenkinsJob retrievedJob = em.find(JenkinsJob.class, job.id);

        // Assert
        assertNotNull(retrievedJob);
        assertEquals("Pharma" + uniqueSuffix, retrievedJob.team);
        assertEquals("v3.21.00", retrievedJob.ormeVersion);
        assertEquals("pharma-tests"+ uniqueSuffix, retrievedJob.name);
        assertEquals("https://ci-jenkins.orbis.dedalus.com/job/HORME/job/DEV_Builds/view/All/job/global_repo/job/DEV_V3.21.00/job/pharma-tests", retrievedJob.url);
    }
}
