package com.dedalus.resource;

import com.dedalus.model.JenkinsJob;
import com.dedalus.repository.JenkinsJobRepository;
import com.dedalus.test.PostgresResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
class JenkinsJobResourceRATest {

    @Inject
    JenkinsJobRepository repository;

    @BeforeEach
    @Transactional
    void setup() {
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
    void testGetAllJobs() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2))
                .body("[0].team", notNullValue())
                .body("[1].team", notNullValue());
    }

    @Test
    void testGetJobById() {
        // First, get all jobs to extract an ID
        String id = given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs")
                .then()
                .statusCode(200)
                .extract().path("[0].id").toString();

        // Then get the specific job by ID
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(Integer.parseInt(id)))
                .body("team", notNullValue())
                .body("name", notNullValue())
                .body("url", notNullValue());
    }

    @Test
    void testGetJobByIdNotFound() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void testGetJobsByTeam() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/team/Pharma")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].team", equalTo("Pharma"));
    }

    @Test
    void testGetJobsByTeamNotFound() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/team/NonExistentTeam")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    void testGetJobsByTeamAndVersion() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/team/Pharma/version/v3.21.00")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].team", equalTo("Pharma"))
                .body("[0].ormeVersion", equalTo("v3.21.00"));
    }

    @Test
    void testGetJobsByTeamAndVersionNotFound() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/team/Pharma/version/v9.99.99")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    void testCreateJob() {
        JenkinsJob newJob = new JenkinsJob("Test", "v3.22.00", "test-job",
                "https://jenkins.example.com/test-job");

        String location = given()
                .auth().basic("admin", "adminpassword")
                .contentType(ContentType.JSON)
                .body(newJob)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .extract().header("Location");

        // Extract the ID from the location
        String id = location.substring(location.lastIndexOf("/") + 1);

        // Verify the job was created
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("team", equalTo("Test"))
                .body("ormeVersion", equalTo("v3.22.00"))
                .body("name", equalTo("test-job"))
                .body("url", equalTo("https://jenkins.example.com/test-job"));
    }

    @Test
    void testUpdateJob() {
        // First, create a job
        JenkinsJob job = new JenkinsJob("UpdateTest", "v3.21.00", "update-test",
                "https://jenkins.example.com/update-test");

        String location = given()
                .auth().basic("admin", "adminpassword")
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
                .auth().basic("admin", "adminpassword")
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
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("ormeVersion", equalTo("v3.22.00"))
                .body("name", equalTo("updated-job"));
    }

    @Test
    void testUpdateJobNotFound() {
        JenkinsJob job = new JenkinsJob("UpdateTest", "v3.21.00", "update-test",
                "https://jenkins.example.com/update-test");

        given()
                .auth().basic("admin", "adminpassword")
                .contentType(ContentType.JSON)
                .body(job)
                .when().put("/api/jenkins-jobs/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteJob() {
        // First, create a job
        JenkinsJob job = new JenkinsJob("DeleteTest", "v3.21.00", "delete-test",
                "https://jenkins.example.com/delete-test");

        String location = given()
                .auth().basic("admin", "adminpassword")
                .contentType(ContentType.JSON)
                .body(job)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .extract().header("Location");

        String id = location.substring(location.lastIndexOf("/") + 1);

        // Now delete it
        given()
                .auth().basic("admin", "adminpassword")
                .when().delete("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(204);

        // Verify it's gone
        given()
                .auth().basic("admin", "adminpassword")
                .when().get("/api/jenkins-jobs/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteJobNotFound() {
        given()
                .auth().basic("admin", "adminpassword")
                .when().delete("/api/jenkins-jobs/999999")
                .then()
                .statusCode(404);
    }

    @Test
    void testUnauthorizedAccess() {
        // Test without authentication
        given()
                .when().get("/api/jenkins-jobs")
                .then()
                .statusCode(401); // Unauthorized
    }
}
