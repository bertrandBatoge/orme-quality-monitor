package com.dedalus.service;

import com.dedalus.client.JenkinsApiClient;
import com.dedalus.model.JenkinsBuild;
import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsBuildRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JenkinsBuildService {

    private final JenkinsBuildRepository repository;
    private final JenkinsJobService jobService;
    private final JenkinsApiClient jenkinsClient;

    @Inject
    public JenkinsBuildService(
            JenkinsBuildRepository repository,
            JenkinsJobService jobService,
            JenkinsApiClient jenkinsClient) {
        this.repository = repository;
        this.jobService = jobService;
        this.jenkinsClient = jenkinsClient;
    }

    public List<JenkinsBuild> getBuildsForJob(Long jobId) {
        return repository.findByJobId(jobId);
    }

    @Transactional
    public List<JenkinsBuild> syncBuildsForJob(Long jobId) {
        Optional<JenkinsJob> jobOptional = jobService.getJobById(jobId);
        if (jobOptional.isEmpty()) {
            return List.of();
        }

        JenkinsJob job = jobOptional.get();
        List<JenkinsBuild> builds = jenkinsClient.fetchBuildsForJob(job);

        // Delete existing builds for this job
        repository.delete("job.id", jobId);

        // Save new builds
        for (JenkinsBuild build : builds) {
            repository.persist(build);
        }

        return builds;
    }

    @Transactional
    public void syncBuildsForAllJobs() {
        List<JenkinsJob> allJobs = jobService.getAllJobs();
        for (JenkinsJob job : allJobs) {
            syncBuildsForJob(job.id);
        }
    }
}