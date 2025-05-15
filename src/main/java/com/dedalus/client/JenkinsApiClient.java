package com.dedalus.client;

import com.dedalus.model.JenkinsBuild;
import com.dedalus.model.JenkinsJob;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.logging.Logger;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JenkinsApiClient {

    private static final Logger LOG = Logger.getLogger(JenkinsApiClient.class);

    private static final String FIELD_NUMBER = "number";
    private static final String FIELD_RESULT = "result";
    private static final String FIELD_TIMESTAMP = "timestamp";
    private static final String FIELD_DURATION = "duration";
    private static final String FIELD_URL = "url";
    private static final String FIELD_BUILDS = "builds";

    @RegisterRestClient
    public interface JenkinsApi {
        @GET
        @Path("/api/json")
        @Produces(MediaType.APPLICATION_JSON)
        String getJobInfo(@QueryParam("tree") String tree);
    }

    public List<JenkinsBuild> fetchBuildsForJob(JenkinsJob job) {
        try {
            String jsonResponse = fetchJsonFromJenkins(job);
            return parseJenkinsBuilds(jsonResponse, job);
        } catch (JenkinsApiException e) {
            LOG.error("Error fetching builds for job: " + job.name, e);
            return new ArrayList<>();
        }
    }

    private String fetchJsonFromJenkins(JenkinsJob job) throws JenkinsApiException {
        try {
            JenkinsApi jenkinsApi = RestClientBuilder.newBuilder()
                    .baseUri(URI.create(job.url))
                    .build(JenkinsApi.class);

            return jenkinsApi.getJobInfo("builds[number,result,timestamp,duration,url]");
        } catch (Exception e) {
            throw new JenkinsApiException("Failed to fetch data from Jenkins API for job: " + job.name, e);
        }
    }

    private List<JenkinsBuild> parseJenkinsBuilds(String jsonResponse, JenkinsJob job) throws JenkinsApiException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);
            JsonNode buildsNode = rootNode.get(FIELD_BUILDS);

            List<JenkinsBuild> builds = new ArrayList<>();

            if (buildsNode == null) {
                LOG.warn("No 'builds' field found in Jenkins API response for job: " + job.name);
                return builds;
            }

            if (!buildsNode.isArray()) {
                throw new JenkinsApiException("'builds' field is not an array in Jenkins API response");
            }

            for (JsonNode buildNode : buildsNode) {
                processBuildNode(buildNode, job, builds);
            }

            return builds;
        } catch (JenkinsApiException e) {
            throw e;
        } catch (Exception e) {
            throw new JenkinsApiException("Failed to parse Jenkins builds data", e);
        }
    }

    private JenkinsBuild createBuildFromNode(JsonNode buildNode, JenkinsJob job) {
        Integer number = extractBuildNumber(buildNode);
        String result = extractBuildResult(buildNode);
        Instant timestamp = extractBuildTimestamp(buildNode);
        Long duration = extractBuildDuration(buildNode);
        String url = extractBuildUrl(buildNode);

        return new JenkinsBuild(job, number, result, timestamp, duration, url);
    }

    private void processBuildNode(JsonNode buildNode, JenkinsJob job, List<JenkinsBuild> builds) {
        try {
            JenkinsBuild build = createBuildFromNode(buildNode, job);
            builds.add(build);
        } catch (Exception e) {
            LOG.warn("Failed to process build data: " + buildNode, e);
        }
    }

    private Integer extractBuildNumber(JsonNode node) {
        return node.has(FIELD_NUMBER) ? node.get(FIELD_NUMBER).asInt() : null;
    }

    private String extractBuildResult(JsonNode node) {
        return node.has(FIELD_RESULT) ? node.get(FIELD_RESULT).asText() : null;
    }

    private Instant extractBuildTimestamp(JsonNode node) {
        if (!node.has(FIELD_TIMESTAMP)) {
            return null;
        }
        try {
            return Instant.ofEpochMilli(node.get(FIELD_TIMESTAMP).asLong());
        } catch (Exception e) {
            LOG.warn("Invalid timestamp format: " + node.get(FIELD_TIMESTAMP), e);
            return null;
        }
    }

    private Long extractBuildDuration(JsonNode node) {
        if (!node.has(FIELD_DURATION)) {
            return 0L;
        }
        try {
            return node.get(FIELD_DURATION).asLong();
        } catch (Exception e) {
            LOG.warn("Invalid duration format: " + node.get(FIELD_DURATION), e);
            return 0L;
        }
    }

    private String extractBuildUrl(JsonNode node) {
        return node.has(FIELD_URL) ? node.get(FIELD_URL).asText() : null;
    }
}
