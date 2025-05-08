package com.dedalus.resource;

import com.dedalus.dto.CreateJenkinsJobRequest;
import com.dedalus.model.JenkinsJob;
import com.dedalus.service.JenkinsJobService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RolesAllowed("admin")
@Path("/api/jenkins-jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JenkinsJobResource {

    private final JenkinsJobService service;

    @Inject
    public JenkinsJobResource(JenkinsJobService service) {
        this.service = service;
    }

    @GET
    public List<JenkinsJob> getAllJobs() {
        return service.getAllJobs();
    }

    @GET
    @Path("/{id}")
    public Response getJobById(@PathParam("id") Long id) {
        return service.getJobById(id)
                .map(job -> Response.ok(job).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/team/{team}")
    public List<JenkinsJob> getJobsByTeam(@PathParam("team") String team) {
        return service.getJobsByTeam(team);
    }

    @GET
    @Path("/team/{team}/version/{version}")
    public List<JenkinsJob> getJobsByTeamAndVersion(
            @PathParam("team") String team,
            @PathParam("version") String version) {
        return service.getJobsByTeamAndOrmeVersion(team, version);
    }

    @POST
    public Response createJob(@Valid CreateJenkinsJobRequest request) {
        JenkinsJob job = new JenkinsJob(
                request.team,
                request.ormeVersion,
                request.name,
                request.url
        );
        JenkinsJob created = service.createJob(job);
        return Response
                .created(UriBuilder.fromResource(JenkinsJobResource.class)
                        .path(String.valueOf(created.id))
                        .build())
                .entity(created)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response updateJob(@PathParam("id") Long id, JenkinsJob job) {
        return service.updateJob(id, job)
                .map(updated -> Response.ok(updated).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteJob(@PathParam("id") Long id) {
        boolean deleted = service.deleteJob(id);
        return deleted
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
