package com.dedalus.resource;

import com.dedalus.model.JenkinsJob;
import com.dedalus.service.JenkinsJobService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@QuarkusTest
public class JenkinsJobResourceTest {

    @InjectMock
    JenkinsJobService service;

    private JenkinsJob job1;
    private JenkinsJob job2;

    @BeforeEach
    public void setup() {
        job1 = new JenkinsJob("Pharma", "v3.21.00", "pharma-tests",
                "https://jenkins.example.com/pharma-tests");
        job1.id = 1L;

        job2 = new JenkinsJob("Presc", "v3.21.00", "presc-tests",
                "https://jenkins.example.com/presc-tests");
        job2.id = 2L;
    }

    @Test
    public void testGetAllJobs() {
        when(service.getAllJobs()).thenReturn(Arrays.asList(job1, job2));

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().get("/api/jenkins-jobs")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(2))
                .body("[0].team", equalTo("Pharma"))
                .body("[1].team", equalTo("Presc"));

        verify(service, times(1)).getAllJobs();
    }

    @Test
    public void testGetJobById() {
        when(service.getJobById(1L)).thenReturn(Optional.of(job1));

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().get("/api/jenkins-jobs/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("team", equalTo("Pharma"))
                .body("name", equalTo("pharma-tests"));

        verify(service, times(1)).getJobById(1L);
    }

    @Test
    public void testGetJobByIdNotFound() {
        when(service.getJobById(99L)).thenReturn(Optional.empty());

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().get("/api/jenkins-jobs/99")
                .then()
                .statusCode(404);

        verify(service, times(1)).getJobById(99L);
    }

    @Test
    public void testGetJobsByTeam() {
        when(service.getJobsByTeam("Pharma")).thenReturn(Collections.singletonList(job1));

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().get("/api/jenkins-jobs/team/Pharma")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].team", equalTo("Pharma"));

        verify(service, times(1)).getJobsByTeam("Pharma");
    }

    @Test
    public void testGetJobsByTeamAndVersion() {
        when(service.getJobsByTeamAndOrmeVersion("Pharma", "v3.21.00"))
                .thenReturn(Collections.singletonList(job1));

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().get("/api/jenkins-jobs/team/Pharma/version/v3.21.00")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(1))
                .body("[0].team", equalTo("Pharma"))
                .body("[0].ormeVersion", equalTo("v3.21.00"));

        verify(service, times(1)).getJobsByTeamAndOrmeVersion("Pharma", "v3.21.00");
    }

    @Test
    public void testCreateJob() {
        JenkinsJob newJob = new JenkinsJob("Pharma", "v3.22.00", "new-test",
                "https://jenkins.example.com/new-test");
        newJob.id = 3L;

        when(service.createJob(any(JenkinsJob.class))).thenReturn(newJob);

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .contentType(ContentType.JSON)
                .body(newJob)
                .when().post("/api/jenkins-jobs")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .header("Location", containsString("/api/jenkins-jobs/3"))
                .body("id", equalTo(3))
                .body("name", equalTo("new-test"));

        verify(service, times(1)).createJob(any(JenkinsJob.class));
    }

    @Test
    public void testUpdateJob() {
        JenkinsJob updatedJob = new JenkinsJob("Pharma", "v3.22.00", "updated-test",
                "https://jenkins.example.com/updated-test");
        updatedJob.id = 1L;

        when(service.updateJob(eq(1L), any(JenkinsJob.class))).thenReturn(Optional.of(updatedJob));

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .contentType(ContentType.JSON)
                .body(updatedJob)
                .when().put("/api/jenkins-jobs/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", equalTo("updated-test"))
                .body("ormeVersion", equalTo("v3.22.00"));

        verify(service, times(1)).updateJob(eq(1L), any(JenkinsJob.class));
    }

    @Test
    public void testUpdateJobNotFound() {
        JenkinsJob updatedJob = new JenkinsJob("Pharma", "v3.22.00", "updated-test",
                "https://jenkins.example.com/updated-test");

        when(service.updateJob(eq(99L), any(JenkinsJob.class))).thenReturn(Optional.empty());

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .contentType(ContentType.JSON)
                .body(updatedJob)
                .when().put("/api/jenkins-jobs/99")
                .then()
                .statusCode(404);

        verify(service, times(1)).updateJob(eq(99L), any(JenkinsJob.class));
    }

    @Test
    public void testDeleteJob() {
        when(service.deleteJob(1L)).thenReturn(true);

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().delete("/api/jenkins-jobs/1")
                .then()
                .statusCode(204);

        verify(service, times(1)).deleteJob(1L);
    }

    @Test
    public void testDeleteJobNotFound() {
        when(service.deleteJob(99L)).thenReturn(false);

        given()
                .auth().basic("admin", "adminpassword") // Add this line for authentication
                .when().delete("/api/jenkins-jobs/99")
                .then()
                .statusCode(404);

        verify(service, times(1)).deleteJob(99L);
    }
}