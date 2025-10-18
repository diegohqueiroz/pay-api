package com.pay.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import com.pay.resources.requests.TransferRequest;
import com.pay.services.TransactionService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransferResource {
    private static final Logger LOG = Logger.getLogger(TransferResource.class);

    @Inject
    private TransactionService transferService;

    @POST
    @Operation(summary = "Realizar uma transferencia.",
               description = "Realizar uma nova transferencia entre contas.")
    @APIResponse(responseCode = "201", description = "Transferencia efetuada com sucesso")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response transfer(TransferRequest request) {

        LOG.debug("Recebida requisição de transferência:");
        LOG.debug("Valor: " + request.getValue());
        LOG.debug("Pagador (ID): " + request.getPayer());
        LOG.debug("Beneficiário (ID): " + request.getPayee());

        String id = transferService.transfer(request);

        return Response.created(UriBuilder.fromResource(TransferResource.class).path(id).build()).build();
    }
}