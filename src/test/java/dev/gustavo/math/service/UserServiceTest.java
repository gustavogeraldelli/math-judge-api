package dev.gustavo.math.service;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.UsernameIsAlreadyInUseException;
import dev.gustavo.math.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("username");
        user.setPassword("password");
    }

    @Nested
    @DisplayName("Find Users")
    class FindUser {
        @Test
        @DisplayName("Should return paginated list of users")
        void findAllShouldReturnPaginatedList() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<User> userPage = new PageImpl<>(List.of(user));
            when(userRepository.findAll(pageable)).thenReturn(userPage);

            Page<User> users = userService.findAll(pageable);

            assertFalse(users.getContent().isEmpty());
            assertEquals(1, users.getTotalElements());
            verify(userRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find a user by their ID successfully")
        void findByIdShouldReturnUserWhenFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            User foundUser = userService.findById(userId);

            assertNotNull(foundUser);
            assertEquals(userId, foundUser.getId());
            verify(userRepository, times(1)).findById(userId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void findByIdShouldThrowExceptionWhenNotFound() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
        }
    }

    @Nested
    @DisplayName("Create User")
    class CreateUser {
        @Test
        @DisplayName("Should create a new user successfully")
        void createShouldSaveAndReturnUserWhenUsernameIsNew() {
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
            when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);

            User createdUser = userService.create(user);

            assertNotNull(createdUser);
            assertEquals("encodedPassword", createdUser.getPassword());
            verify(userRepository, times(1)).existsByUsername(user.getUsername());
            verify(passwordEncoder, times(1)).encode("password");
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should throw UsernameIsAlreadyInUseException when username is already in use")
        void createShouldThrowExceptionWhenUsernameAlreadyInUse() {
            when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

            assertThrows(UsernameIsAlreadyInUseException.class, () -> userService.create(user));
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Update User")
    class UpdateUser {
        @Test
        @DisplayName("Should update user successfully")
        void updateShouldUpdateAndReturnUserWhenSuccessful() {
            User newNickname = new User();
            newNickname.setNickname("newNickname");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            User updatedUser = userService.update(userId, newNickname);

            assertNotNull(updatedUser);
            assertEquals("newNickname", updatedUser.getNickname());
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should throw UsernameIsAlreadyInUseException when new username is already in use")
        void updateShouldThrowExceptionWhenNewUsernameIsAlreadyInUse() {
            User newUsername = new User();
            newUsername.setUsername("existingUsername");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByUsername(newUsername.getUsername())).thenReturn(true);

            assertThrows(UsernameIsAlreadyInUseException.class, () -> userService.update(userId, newUsername));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void updateShouldThrowExceptionWhenUserNotFound() {
            User newUsername = new User();
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.update(userId, newUsername));
            verify(userRepository, never()).save(any(User.class));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {
        @Test
        @DisplayName("Should delete a user successfully")
        void deleteShouldRemoveUserWhenUserExists() {
            when(userRepository.existsById(userId)).thenReturn(true);
            doNothing().when(userRepository).deleteById(userId);

            userService.delete(userId);

            verify(userRepository, times(1)).existsById(userId);
            verify(userRepository, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void deleteShouldThrowExceptionWhenUserNotFound() {
            when(userRepository.existsById(userId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> userService.delete(userId));
            verify(userRepository, never()).deleteById(any(UUID.class));
        }
    }

}