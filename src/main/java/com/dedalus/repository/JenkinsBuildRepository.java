package com.dedalus.repository;

import com.dedalus.model.JenkinsBuild;
import com.dedalus.model.JenkinsJob;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class JenkinsBuildRepository implements PanacheRepository<JenkinsBuild> {

    public List<JenkinsBuild> findByJob(JenkinsJob job) {
        return list("job", job);
    }

    public List<JenkinsBuild> findByJobId(Long jobId) {
        return list("job.id", jobId);
    }
}
