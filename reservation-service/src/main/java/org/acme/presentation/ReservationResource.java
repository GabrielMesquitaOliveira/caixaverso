package org.acme.presentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.ReservationDto;
import org.acme.application.AvailableSlotDto;
import org.acme.application.CreateReservationUseCase;
import org.acme.application.GetAvailableSlotsUseCase;
import java.time.LocalDate;
import java.util.List;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;

    public ReservationResource(CreateReservationUseCase createReservationUseCase,
            GetAvailableSlotsUseCase getAvailableSlotsUseCase) {
        this.createReservationUseCase = createReservationUseCase;
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
    }

    @POST
    public Response createReservation(ReservationDto reservationDto) {
        ReservationDto created = createReservationUseCase.execute(reservationDto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/available")
    public Response getAvailableSlots(@QueryParam("date") String dateStr, @QueryParam("resource") String resourceName) {
        LocalDate date = LocalDate.parse(dateStr);
        List<AvailableSlotDto> slots = getAvailableSlotsUseCase.execute(date, resourceName);
        return Response.ok(slots).build();
    }
}
