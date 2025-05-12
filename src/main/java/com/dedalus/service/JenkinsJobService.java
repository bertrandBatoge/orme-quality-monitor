package com.dedalus.service;

import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsJobRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class JenkinsJobService {

    private final JenkinsJobRepository repository;

    @Inject
    public JenkinsJobService(JenkinsJobRepository repository) {
        this.repository = repository;
    }

    public List<JenkinsJob> getAllJobs() {
        return repository.listAll();
    }

    public List<JenkinsJob> getJobsByTeam(String team) {
        return repository.findByTeam(team);
    }

    public List<JenkinsJob> getJobsByTeamAndOrmeVersion(String team, String ormeVersion) {
        return repository.findByTeamAndOrmeVersion(team, ormeVersion);
    }

    public Optional<JenkinsJob> getJobById(Long id) {
        return Optional.ofNullable(repository.findById(id));
    }

    @Transactional
    public JenkinsJob createJob(JenkinsJob job) {
        var query = repository.find(
                "team = ?1 and ormeVersion = ?2 and name = ?3 and url = ?4",
                job.team, job.ormeVersion, job.name, job.url
        );

        if (query != null) {
            List<JenkinsJob> existingJobs = query.list();
            if (!existingJobs.isEmpty()) {
                return existingJobs.get(0);
            }
        }

        repository.persist(job);
        return job;
    }

    @Transactional
    public Optional<JenkinsJob> updateJob(Long id, JenkinsJob updatedJob) {
        JenkinsJob existingJob = repository.findById(id);
        if (existingJob == null) {
            return Optional.empty();
        }

        existingJob.team = updatedJob.team;
        existingJob.ormeVersion = updatedJob.ormeVersion;
        existingJob.name = updatedJob.name;
        existingJob.url = updatedJob.url;

        return Optional.of(existingJob);
    }

    @Transactional
    public boolean deleteJob(Long id) {
        return repository.deleteById(id);
    }
}
