package org.acme.domain.port;

import org.acme.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findUserById(String id);

    List<User> findAllUsers();
}
