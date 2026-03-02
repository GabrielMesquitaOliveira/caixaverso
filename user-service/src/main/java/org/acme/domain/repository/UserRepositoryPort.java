package org.acme.domain.repository;

import java.util.List;
import java.util.Optional;

import org.acme.domain.entity.User;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findUserById(String id);

    List<User> findAllUsers();
}
