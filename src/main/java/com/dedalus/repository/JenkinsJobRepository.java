package com.dedalus.repository;

import com.dedalus.model.JenkinsJob;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class JenkinsJobRepository implements PanacheRepository<JenkinsJob> {

    public List<JenkinsJob> findByTeam(String team) {
        return list("team", team);
    }

    public List<JenkinsJob> findByTeamAndOrmeVersion(String team, String ormeVersion) {
        return list("team = ?1 and ormeVersion = ?2", team, ormeVersion);
    }

    public List<JenkinsJob> findByOrmeVersion(String ormeVersion) {
        return list("ormeVersion", ormeVersion);
    }
}
