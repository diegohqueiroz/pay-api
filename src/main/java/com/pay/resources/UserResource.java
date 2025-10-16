package com.pay.resources;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.logging.Logger;

import com.pay.resources.dtos.UserDTO;
import com.pay.services.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

@Path("/users")
public class UserResource {
    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retorna a lista de usuários.",
               description = "Retorna a lista com todos os usuários.")
    @APIResponse(responseCode = "200", description = "Operação bem-sucedida")
    @APIResponse(responseCode = "422", description = "Erro de negocio")
    public Response get() {
        LOG.debugf("[get]");
        return Response.ok().entity(service.getAll()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retorna um usuário.",
    description = "Retorna um usuário específico com base no ID.")
    @APIResponse(responseCode = "200", description = "Operação bem-sucedida")
    @APIResponse(responseCode = "422", description = "Erro de negocio")
    public Response get(@PathParam("id") String id) {
        LOG.debugf("[get] {0}", id);
        UserDTO user = service.getById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build(); 
        }
        return Response.ok().entity(user).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cria um novo usuário.",
               description = "Cria um novo usuário no sistema.")
    @APIResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response create(UserDTO userDTO) {
        LOG.debugf("[create] {0}", userDTO.getName());
        
        String id = service.create(userDTO);
        
        return Response.created(UriBuilder.fromResource(UserResource.class).path(id).build()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Atualizar um usuário.",
               description = "Atualizar um usuário específico com base no ID.")
    @APIResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @APIResponse(responseCode = "404", description = "Usuário não encontrado para atualizar")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response update(UserDTO userDTO, @PathParam("id") String id) {
        LOG.debugf("[update] {0}", userDTO.getName());
        
        service.update(userDTO, id);
        
        return Response.ok(UriBuilder.fromResource(UserResource.class).path(id).build()).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Deletar um usuário.",
               description = "Deletar um usuário específico com base no ID.")
    @APIResponse(responseCode = "204", description = "Usuário deleta com sucesso")
    @APIResponse(responseCode = "422", description = "Erro de validação")
    public Response delete(@PathParam("id") String id) {
        LOG.debugf("[delete] {0}", id);
        
        service.delete(id);
        
        return Response.noContent().build();
    }
}
