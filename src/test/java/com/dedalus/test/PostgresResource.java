package com.dedalus.test;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class PostgresResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("orme_monitor")
            .withUsername("postgres")
            .withPassword("postgres");

    @Override
    public Map<String, String> start() {
        POSTGRES.start();
        return Map.of(
                "quarkus.datasource.jdbc.url", POSTGRES.getJdbcUrl(),
                "quarkus.datasource.username", POSTGRES.getUsername(),
                "quarkus.datasource.password", POSTGRES.getPassword(),
                // Disable Flyway for tests since we're using a fresh container
                "quarkus.flyway.migrate-at-start", "false",
                // Use create-drop for tests to set up schema
                "quarkus.hibernate-orm.database.generation", "drop-and-create"
        );
    }

    @Override
    public void stop() {
        if (POSTGRES.isRunning()) {
            POSTGRES.stop();
        }
    }
}
