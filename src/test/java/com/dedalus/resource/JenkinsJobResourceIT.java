package com.dedalus.resource;

import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsJobRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class JenkinsJobResourceIT {

    @Inject
    JenkinsJobRepository repository;

    @BeforeEach
    @Transactional
    public void setup() {
        // Clear all data before each test
        repository.deleteAll();

        // Add test data
        JenkinsJob job1 = new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                "https://jenkins.example.com/pharma-tests");
        JenkinsJob job2 = new JenkinsJob("Presc", "v3.21.00", "presc-tests",
                "https://jenkins.example.com/presc-tests");

        repository.persist(job1);
        repository.persist(job2);
    }

    @Test
    public void testGetAllJobs() {
        given()
                .when().get("/api/jenkins-jobs")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2));
    }

    @Test
    public void testGetJobsByTeam() {
        given()
                .when().get("/api/jenkins-jobs/team/Pharma")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].team", equalTo("Pharma"));
    }

    @Test
    public void testCreateAndGetJob() {
        // Create a new job
        JenkinsJob newJob = new JenkinsJob("Test", "v3.22.00", "test-job",
                "https://jenkins.example.com/test-job");

        String location = given()
                .contentType(ContentType.JSON)
                .body(newJob)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .extract().header("Location");

        // Extract the ID from the location
        String id = location.substring(location.lastIndexOf("/") + 1);

        // Get the created job
        given()
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("team", equalTo("Test"))
                .body("ormeVersion", equalTo("v3.22.00"))
                .body("name", equalTo("test-job"));
    }

    @Test
    public void testUpdateJob() {
        // First, create a job
        JenkinsJob job = new JenkinsJob("UpdateTest", "v3.21.00", "update-test",
                "https://jenkins.example.com/update-test");

        String location = given()
                .contentType(ContentType.JSON)
                .body(job)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .extract().header("Location");

        String id = location.substring(location.lastIndexOf("/") + 1);

        // Now update it
        job.ormeVersion = "v3.22.00";
        job.name = "updated-job";

        given()
                .contentType(ContentType.JSON)
                .body(job)
                .when().put("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("ormeVersion", equalTo("v3.22.00"))
                .body("name", equalTo("updated-job"));

        // Verify the update
        given()
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("ormeVersion", equalTo("v3.22.00"))
                .body("name", equalTo("updated-job"));
    }

    @Test
    public void testDeleteJob() {
        // First, create a job
        JenkinsJob job = new JenkinsJob("DeleteTest", "v3.21.00", "delete-test",
                "https://jenkins.example.com/delete-test");

        String location = given()
                .contentType(ContentType.JSON)
                .body(job)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .extract().header("Location");

        String id = location.substring(location.lastIndexOf("/") + 1);

        // Now delete it
        given()
                .when().delete("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(204);

        // Verify it's gone
        given()
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(404);
    }
}