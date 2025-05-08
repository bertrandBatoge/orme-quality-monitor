package com.dedalus.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateJenkinsJobRequest {

    @NotBlank(message = "Team is required")
    public String team;

    @NotBlank(message = "Orme version is required")
    public String ormeVersion;

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "URL is required")
    public String url;
}

