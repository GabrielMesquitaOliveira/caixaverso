package org.acme.presentation;

import jakarta.ws.rs.core.Response;
import org.acme.domain.*;
import org.acme.domain.exceptions.BusinessHoursViolationException;
import org.acme.domain.exceptions.InvalidDurationException;
import org.acme.domain.exceptions.InvalidTimeSlotException;
import org.acme.domain.exceptions.SlotAlreadyBookedException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ReservationExceptionHandler {

    @ServerExceptionMapper
    public Response handleInvalidDurationException(InvalidDurationException ex) {
        return handleValidationException(ex);
    }

    @ServerExceptionMapper
    public Response handleInvalidTimeSlotException(InvalidTimeSlotException ex) {
        return handleValidationException(ex);
    }

    @ServerExceptionMapper
    public Response handleBusinessHoursViolationException(BusinessHoursViolationException ex) {
        return handleValidationException(ex);
    }

    @ServerExceptionMapper
    public Response handleSlotAlreadyBookedException(SlotAlreadyBookedException ex) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    private Response handleValidationException(RuntimeException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }

    public record ErrorResponse(String message) {
    }
}
