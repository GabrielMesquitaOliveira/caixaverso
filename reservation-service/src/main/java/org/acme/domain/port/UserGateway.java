package org.acme.domain.port;

import java.util.Optional;

public interface UserGateway {
    Optional<String> findUsernameById(String userId);
}
