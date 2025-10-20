package com.pay.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import com.pay.resources.responses.AccountResponse;
import com.pay.services.AccountService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/v1/users")
public class AccountResource {
    private static final Logger LOG = Logger.getLogger(AccountResource.class);

    @Inject
    AccountService service;

    @GET
    @Path("/{idUser}/account")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retorna a lista de contas de um usuário.",
               description = "Retorna a lista de contas de um usuário.")
    @APIResponse(responseCode = "200", description = "Operação bem-sucedida")
    @APIResponse(responseCode = "422", description = "Erro de negocio")
    public Response get(@PathParam("idUser") Long idUser) {
        LOG.debugf("[get]");
        return Response.ok().entity(service.getByUserId(idUser)).build();
    }
   
}
