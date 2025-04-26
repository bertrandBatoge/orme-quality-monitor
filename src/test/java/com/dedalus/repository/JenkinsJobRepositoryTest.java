package com.dedalus.repository;

import com.dedalus.model.JenkinsJob;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class JenkinsJobRepositoryTest {

    @Inject
    JenkinsJobRepository repository;

    @BeforeEach
    @Transactional
    public void setup() {
        // Clear all data before each test
        repository.deleteAll();
    }

    @Test
    @Transactional
    public void testFindByTeam() {
        // Create and persist test data
        JenkinsJob job1 = new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                "https://jenkins.example.com/pharma-tests");
        JenkinsJob job2 = new JenkinsJob("Pharma", "v3.20.00", "pharma-tests-old",
                "https://jenkins.example.com/pharma-tests-old");
        JenkinsJob job3 = new JenkinsJob("Presc", "v3.21.00", "presc-tests",
                "https://jenkins.example.com/presc-tests");

        repository.persist(job1);
        repository.persist(job2);
        repository.persist(job3);

        // Test findByTeam
        List<JenkinsJob> pharmaJobs = repository.findByTeam("Pharma");
        assertEquals(2, pharmaJobs.size());
        assertTrue(pharmaJobs.stream().allMatch(job -> "Pharma".equals(job.team)));

        // Test findByTeamAndOrmeVersion
        List<JenkinsJob> pharmaV321Jobs = repository.findByTeamAndOrmeVersion("Pharma", "v3.21.00");
        assertEquals(1, pharmaV321Jobs.size());
        assertEquals("pharma-tests", pharmaV321Jobs.get(0).name);

        // Test findByOrmeVersion
        List<JenkinsJob> v321Jobs = repository.findByOrmeVersion("v3.21.00");
        assertEquals(2, v321Jobs.size());
        assertTrue(v321Jobs.stream().allMatch(job -> "v3.21.00".equals(job.ormeVersion)));
    }
}
