package org.acme.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.acme.domain.User;
import org.acme.domain.port.UserRepositoryPort;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserJpaEntity, String>, UserRepositoryPort {

    @Override
    public User save(User user) {
        UserJpaEntity entity = toJpaEntity(user);
        persist(entity);
        return toDomainEntity(entity);
    }

    // PanacheRepositoryBase already has findByIdOptional, but the
    // UserRepositoryPort expects User domain entities,
    // so let's separate them explicitly.
    @Override
    public Optional<User> findUserById(String id) {
        return findByIdOptional(id).map(this::toDomainEntity);
    }

    @Override
    public List<User> findAllUsers() {
        return listAll().stream().map(this::toDomainEntity).collect(Collectors.toList());
    }

    private UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setFullName(user.getFullName());
        return entity;
    }

    private User toDomainEntity(UserJpaEntity entity) {
        return new User(entity.getId(), entity.getUsername(), entity.getEmail(), entity.getFullName());
    }
}
