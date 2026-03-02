package org.acme.application;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.acme.domain.User;
import org.acme.domain.port.UserRepositoryPort;

@ApplicationScoped
public class CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public CreateUserUseCase(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    public UserDto execute(UserDto userDto) {
        String id = UUID.randomUUID().toString();
        User user = new User(id, userDto.username(), userDto.email(), userDto.fullName());
        User savedUser = userRepositoryPort.save(user);
        return userMapper.toDto(savedUser);
    }
}
