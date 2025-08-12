package dev.gustavo.math.service;

import dev.gustavo.math.entity.User;
import dev.gustavo.math.exception.user.UsernameIsAlreadyInUseException;
import dev.gustavo.math.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameIsAlreadyInUseException(String.format(user.getUsername()));

        return userRepository.save(user);
    }

    public User update(UUID id, User user) {
        var existingUser = findById(id);

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            if (userRepository.existsByUsername(user.getUsername()))
                throw new UsernameIsAlreadyInUseException(user.getUsername());
            existingUser.setUsername(user.getUsername());
        }

        if (user.getPassword() != null && !user.getPassword().isBlank())
            existingUser.setPassword(user.getPassword());

        if (user.getNickname() != null && !user.getNickname().isBlank())
            existingUser.setNickname(user.getNickname());

        return userRepository.save(existingUser);
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id))
            throw new EntityNotFoundException(String.format("User with id %s not found", id));

        userRepository.deleteById(id);
    }

}
