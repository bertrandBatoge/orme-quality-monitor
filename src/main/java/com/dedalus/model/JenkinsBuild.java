package com.dedalus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "jenkins_builds")
public class JenkinsBuild extends PanacheEntity {

    @ManyToOne
    @JoinColumn(name = "jenkins_job_id")
    public JenkinsJob job;

    @Column(name = "build_number")
    public Integer buildNumber;

    @Column
    public String result;

    @Column
    public Instant timestamp;

    @Column
    public Long duration;

    @Column
    public String url;

    // Default constructor for JPA
    public JenkinsBuild() {}

    public JenkinsBuild(JenkinsJob job, Integer buildNumber, String result,
                        Instant timestamp, Long duration, String url) {
        this.job = job;
        this.buildNumber = buildNumber;
        this.result = result;
        this.timestamp = timestamp;
        this.duration = duration;
        this.url = url;
    }
}
