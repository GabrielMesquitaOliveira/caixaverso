package org.acme.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.acme.domain.User;
import org.acme.domain.UserNotFoundException;
import org.acme.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @Mock
    UserRepositoryPort userRepositoryPort;

    GetUserUseCase useCase;

    // For this simple test, we could mock the mapper or use a real instance if it's
    // simple enough.
    // Since MapStruct generates it, mocking is safer without CDI.
    @Mock
    UserMapper userMapper;

    @BeforeEach
    void setUp() {
        useCase = new GetUserUseCase(userRepositoryPort, userMapper);
    }

    @Test
    void shouldReturnUserWhenFound() {
        // Arrange
        User existingUser = new User("123", "gabriel", "gabriel@test.com", "Gabriel H");
        UserDto expectedDto = new UserDto("123", "gabriel", "gabriel@test.com", "Gabriel H");

        when(userRepositoryPort.findUserById("123")).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

        // Act
        UserDto result = useCase.getUserById("123");

        // Assert
        assertNotNull(result);
        assertEquals("Gabriel H", result.fullName());
        verify(userRepositoryPort).findUserById("123");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        // Arrange
        when(userRepositoryPort.findUserById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> useCase.getUserById("999"));
        verify(userMapper, never()).toDto(any());
    }
}
