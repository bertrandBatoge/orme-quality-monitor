package com.dedalus.service;

import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsJobRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class JenkinsJobServiceTest {

    @InjectMock
    JenkinsJobRepository repository;

    @Inject
    JenkinsJobService service;

    private JenkinsJob job1;
    private JenkinsJob job2;

    @BeforeEach
    public void setup() {
        job1 = new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                "https://jenkins.example.com/pharma-tests");
        job1.id = 1L;

        job2 = new JenkinsJob("Presc", "v3.21.00", "presc-tests",
                "https://jenkins.example.com/presc-tests");
        job2.id = 2L;
    }

    @Test
    public void testGetAllJobs() {
        when(repository.listAll()).thenReturn(Arrays.asList(job1, job2));

        List<JenkinsJob> jobs = service.getAllJobs();

        assertEquals(2, jobs.size());
        verify(repository).listAll();
    }

    @Test
    public void testGetJobsByTeam() {
        when(repository.findByTeam("Pharma")).thenReturn(Collections.singletonList(job1));

        List<JenkinsJob> jobs = service.getJobsByTeam("Pharma");

        assertEquals(1, jobs.size());
        assertEquals("Pharma", jobs.get(0).team);
        verify(repository).findByTeam("Pharma");
    }

    @Test
    public void testGetJobById() {
        when(repository.findById(1L)).thenReturn(job1);

        Optional<JenkinsJob> job = service.getJobById(1L);

        assertTrue(job.isPresent());
        assertEquals("pharma-tests", job.get().name);
        verify(repository).findById(1L);
    }

    @Test
    public void testCreateJob() {
        JenkinsJob newJob = new JenkinsJob("Pharma", "v3.22.00", "new-test",
                "https://jenkins.example.com/new-test");

        service.createJob(newJob);

        verify(repository).persist(newJob);
    }

    @Test
    public void testUpdateJob() {
        JenkinsJob updatedJob = new JenkinsJob("Pharma", "v3.22.00", "updated-test",
                "https://jenkins.example.com/updated-test");

        when(repository.findById(1L)).thenReturn(job1);

        Optional<JenkinsJob> result = service.updateJob(1L, updatedJob);

        assertTrue(result.isPresent());
        assertEquals("updated-test", result.get().name);
        assertEquals("v3.22.00", result.get().ormeVersion);
    }

    @Test
    public void testUpdateNonExistentJob() {
        JenkinsJob updatedJob = new JenkinsJob("Pharma", "v3.22.00", "updated-test",
                "https://jenkins.example.com/updated-test");

        when(repository.findById(99L)).thenReturn(null);

        Optional<JenkinsJob> result = service.updateJob(99L, updatedJob);

        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteJob() {
        when(repository.deleteById(1L)).thenReturn(true);

        boolean result = service.deleteJob(1L);

        assertTrue(result);
        verify(repository).deleteById(1L);
    }
}
