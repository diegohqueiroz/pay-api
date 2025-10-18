package com.pay.exceptions;

// ValidationExceptionMapper.java

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Collections;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        TransactionException error = new TransactionException("Falha na validação de entrada: " + errorMessage);
        var errorBody = Collections.singletonMap("erro", error.getMessage());

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorBody)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}