package com.pay.resources;

import com.pay.resources.clients.AutorizationClient;
import com.pay.resources.requests.AutorizationRequest;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Alternative
@Priority(1)
public class MockAutorizationClient implements AutorizationClient{

    @Override
    public Uni<Response> authorize(AutorizationRequest request) {
        if(request.idAccount() != 99){
            return Uni.createFrom().item(Response.status(Response.Status.OK).build());
        }
        throw new WebApplicationException("Simulando erro de autorização");
    }
}
