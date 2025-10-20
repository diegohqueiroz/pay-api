package com.pay.exceptions;

import com.pay.resources.responses.ErrorResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TransactionExceptionMapper implements ExceptionMapper<TransactionException> {

    private static final int HTTP_STATUS_422 = 422;

    @Override
    public Response toResponse(TransactionException exception) {
        var errorBody = new ErrorResponse(HTTP_STATUS_422, exception.getMessage());
        
        return Response
            .status(HTTP_STATUS_422)
            .entity(errorBody)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}