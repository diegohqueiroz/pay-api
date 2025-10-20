package com.pay.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import com.pay.resources.requests.MovimentRequest;
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

@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {
    private static final Logger LOG = Logger.getLogger(TransactionResource.class);

    @Inject
    private TransactionService transferService;

    @POST
    @Path("/transfer")
    @Operation(summary = "Realizar uma transferencia.",
               description = "Realizar uma nova transferencia entre contas.")
    @APIResponse(responseCode = "201", description = "Transferencia efetuada com sucesso")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response transfer(TransferRequest request) {
        LOG.info("[transfer] Recebida requisição de transferência:");
        LOG.debug("[transfer] Valor: " + request.getValue());
        LOG.debug("[transfer] Pagador (ID): " + request.getPayer());
        LOG.debug("[transfer] Beneficiário (ID): " + request.getPayee());

        String id = transferService.transfer(request);

        return Response.created(UriBuilder.fromResource(TransactionResource.class).path(id).build()).build();
    }

    @POST
    @Path("/debit")
    @Operation(summary = "Realizar um saque.",
               description = "Realizar um saque da conta.")
    @APIResponse(responseCode = "201", description = "Saque efetuado com sucesso")
    @APIResponse(responseCode = "400", description = "Erro na requisição")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response debit(MovimentRequest request) {
        LOG.info("[debit] Recebida requisição de transferência:");
        LOG.debug("[debit] Valor: " + request.getValue());
        LOG.debug("[debit] Conta de debito (ID): " + request.getAccount());

        String id = transferService.debit(request);

        return Response.created(UriBuilder.fromResource(TransactionResource.class).path(id).build()).build();
    }

    @POST
    @Path("/credit")
    @Operation(summary = "Realizar um deposito.",
               description = "Realizar um deposito da conta.")
    @APIResponse(responseCode = "201", description = "Deposito efetuado com sucesso")
    @APIResponse(responseCode = "400", description = "Erro na requisição")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response credit(MovimentRequest request) {
        LOG.info("[credit] Recebida requisição de transferência:");
        LOG.debug("[credit] Valor: " + request.getValue());
        LOG.debug("[credit] Conta de credito (ID): " + request.getAccount());

        String id = transferService.credit(request);

        return Response.created(UriBuilder.fromResource(TransactionResource.class).path(id).build()).build();
    }
}