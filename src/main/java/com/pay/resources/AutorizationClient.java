package com.pay.resources;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.pay.resources.requests.AutorizationRequest;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.inject.Default;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Default
@RegisterRestClient(configKey = "autorization-api")
public interface AutorizationClient {

    @POST
    @Path("/authorize")
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Response> authorize(AutorizationRequest request);
}