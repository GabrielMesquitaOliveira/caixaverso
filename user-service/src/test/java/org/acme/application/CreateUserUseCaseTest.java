package org.acme.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.acme.domain.entity.User;
import org.acme.domain.repository.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    UserRepositoryPort userRepositoryPort;

    @Mock
    UserMapper userMapper;

    CreateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateUserUseCase(userRepositoryPort, userMapper);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        UserDto incomingDto = new UserDto(null, "johndoe", "john@test.com", "John Doe");
        User savedUser = new User("some-uuid", "johndoe", "john@test.com", "John Doe");
        UserDto expectedDto = new UserDto("some-uuid", "johndoe", "john@test.com", "John Doe");

        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        // Act
        UserDto result = useCase.execute(incomingDto);

        // Assert
        assertNotNull(result.id());
        assertEquals("johndoe", result.username());
        verify(userRepositoryPort).save(any(User.class));
        verify(userMapper).toDto(savedUser);
    }

    @Test
    void shouldThrowWhenUsernameIsMissing() {
        // Arrange
        UserDto invalidDto = new UserDto(null, null, "john@test.com", "John Doe");

        // Act & Assert (User constructor validation will throw)
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(invalidDto));
        verify(userRepositoryPort, never()).save(any());
    }
}
