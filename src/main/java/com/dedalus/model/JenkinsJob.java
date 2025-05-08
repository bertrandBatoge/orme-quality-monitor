package com.dedalus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "jenkins_jobs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"team", "orme_version", "name", "url"})
})
@SuppressWarnings({"JpaDataSourceORMInspection"})
public class JenkinsJob extends PanacheEntity {

    @Column(nullable = false)
    public String team;

    @Column(name = "orme_version")
    public String ormeVersion;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false)
    public String url;

    // Default constructor required by JPA
    public JenkinsJob() {
    }

    public JenkinsJob(String team, String ormeVersion, String name, String url) {
        this.team = team;
        this.ormeVersion = ormeVersion;
        this.name = name;
        this.url = url;
    }
}