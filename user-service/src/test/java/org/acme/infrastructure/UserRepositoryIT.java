package org.acme.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.acme.domain.entity.User;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class UserRepositoryIT {

    @Inject
    UserRepository repository;

    @Test
    @Transactional
    void shouldSaveAndFindUserById() {
        User user = new User("it-uuid-1", "gabriel", "gab@test.com", "Gabriel H");

        User saved = repository.save(user);

        assertThat(saved.getId()).isEqualTo("it-uuid-1");
        assertThat(saved.getUsername()).isEqualTo("gabriel");
        assertThat(saved.getEmail()).isEqualTo("gab@test.com");
        assertThat(saved.getFullName()).isEqualTo("Gabriel H");
    }

    @Test
    @Transactional
    void shouldReturnEmptyWhenUserNotFound() {
        Optional<User> result = repository.findUserById("non-existent-id");

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void shouldFindUserByIdAfterSave() {
        User user = new User("it-uuid-2", "johndoe", "john@test.com", "John Doe");
        repository.save(user);

        Optional<User> found = repository.findUserById("it-uuid-2");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    @Transactional
    void shouldFindAllUsers() {
        repository.save(new User("it-uuid-3", "alice", "alice@test.com", "Alice"));
        repository.save(new User("it-uuid-4", "bob", "bob@test.com", "Bob"));

        List<User> users = repository.findAllUsers();

        // DevServices isolates each test run, but we assert at minimum our 2 records
        // exist
        assertThat(users).extracting(User::getUsername)
                .contains("alice", "bob");
    }
}
