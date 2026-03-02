package org.acme.application;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;
import org.acme.domain.UserNotFoundException;
import org.acme.domain.port.UserRepositoryPort;

@ApplicationScoped
public class GetUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;

    public GetUserUseCase(UserRepositoryPort userRepositoryPort, UserMapper userMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;
    }

    public UserDto getUserById(String id) {
        return userRepositoryPort.findUserById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public List<UserDto> getAllUsers() {
        return userRepositoryPort.findAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
