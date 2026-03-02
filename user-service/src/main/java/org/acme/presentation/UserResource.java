package org.acme.presentation;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.UserDto;
import org.acme.application.CreateUserUseCase;
import org.acme.application.GetUserUseCase;
import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserResource(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @POST
    public Response createUser(UserDto userDto) {
        UserDto created = createUserUseCase.execute(userDto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") String id) {
        UserDto user = getUserUseCase.getUserById(id);
        return Response.ok(user).build();
    }

    @GET
    public Response getAllUsers() {
        List<UserDto> users = getUserUseCase.getAllUsers();
        return Response.ok(users).build();
    }
}
