package com.dedalus.resource;

import com.dedalus.model.JenkinsBuild;
import com.dedalus.service.JenkinsBuildService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RolesAllowed("admin")
@Path("/api/jenkins-builds")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JenkinsBuildResource {

    private final JenkinsBuildService service;

    @Inject
    public JenkinsBuildResource(JenkinsBuildService service) {
        this.service = service;
    }

    @GET
    @Path("/job/{jobId}")
    public List<JenkinsBuild> getBuildsForJob(@PathParam("jobId") Long jobId) {
         return service.getBuildsForJob(jobId);
    }

    @POST
    @Path("/job/{jobId}/sync")
    public Response syncBuildsForJob(@PathParam("jobId") Long jobId) {
        List<JenkinsBuild> builds = service.syncBuildsForJob(jobId);
        return Response.ok(builds).build();
    }

    @POST
    @Path("/sync-all")
    public Response syncAllBuilds() {
        service.syncBuildsForAllJobs();
        return Response.ok().build();
    }
}
