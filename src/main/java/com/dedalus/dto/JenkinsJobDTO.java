package com.dedalus.dto;

import com.dedalus.model.JenkinsJob;
import jakarta.validation.constraints.NotBlank;

public class JenkinsJobDTO {

    @NotBlank(message = "Team is required")
    private String team;

    @NotBlank(message = "Orme version is required")
    private String ormeVersion;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "URL is required")
    private String url;

    public JenkinsJobDTO() {}

    public JenkinsJobDTO(String team, String ormeVersion, String name, String url) {
        this.team = team;
        this.ormeVersion = ormeVersion;
        this.name = name;
        this.url = url;
    }

    public JenkinsJob toEntity() {
        return new JenkinsJob(this.team, this.ormeVersion, this.name, this.url);
    }

    public static JenkinsJobDTO fromEntity(JenkinsJob entity) {
        return new JenkinsJobDTO(
                entity.team,
                entity.ormeVersion,
                entity.name,
                entity.url
        );
    }
}

