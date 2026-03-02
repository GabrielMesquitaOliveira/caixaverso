package org.acme.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.port.UserGateway;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.Optional;

@ApplicationScoped
public class UserGatewayAdapter implements UserGateway {

    private final UserRestClient userRestClient;

    public UserGatewayAdapter(@RestClient UserRestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    @Override
    public Optional<String> findUsernameById(String userId) {
        try {
            UserRestClient.UserDtoResponse response = userRestClient.getUserById(userId);
            if (response != null) {
                return Optional.of(response.username());
            }
        } catch (Exception e) {
            // In a real scenario, handle 404 vs 500 appropriately. For now, 404/others mean
            // not found/not valid.
            return Optional.empty();
        }
        return Optional.empty();
    }
}
