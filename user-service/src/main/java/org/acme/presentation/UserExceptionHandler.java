package org.acme.presentation;

import jakarta.ws.rs.core.Response;

import org.acme.domain.exceptions.UserNotFoundException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class UserExceptionHandler {

    @ServerExceptionMapper
    public Response handleUserNotFoundException(UserNotFoundException ex) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    public record ErrorResponse(String message) {
    }
}
