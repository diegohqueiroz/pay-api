package com.pay.resources;

import com.pay.resources.clients.NotificationClient;
import com.pay.resources.requests.NotificationRequest;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
@Alternative
@Priority(1)
public class MockNotificationClientTest implements NotificationClient{

    @Override
    public Uni<Response> notify(NotificationRequest request) {
        if(request.getEmail().equals("")){
            return Uni.createFrom().item(Response.status(Response.Status.OK).build());
        }
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }
}
