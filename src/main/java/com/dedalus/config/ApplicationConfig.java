package com.dedalus.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "orme.monitor")
public interface ApplicationConfig {

    @WithDefault("true")
    boolean enableDataInitialization();

    @WithDefault("Development")
    String environment();

    JenkinsConfig jenkins();

    interface JenkinsConfig {
        @WithDefault("https://ci-jenkins.orbis.dedalus.com")
        String baseUrl();

        @WithDefault("60")
        int cacheTimeoutMinutes();
    }
}
