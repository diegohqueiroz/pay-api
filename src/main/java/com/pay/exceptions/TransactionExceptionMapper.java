package com.pay.exceptions;

import java.util.Collections;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TransactionExceptionMapper implements ExceptionMapper<TransactionException> {

    private static final int HTTP_STATUS_422 = 422;

    @Override
    public Response toResponse(TransactionException exception) {
        var errorBody = Collections.singletonMap("erro", exception.getMessage());
        
        return Response
            .status(HTTP_STATUS_422)
            .entity(errorBody)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}