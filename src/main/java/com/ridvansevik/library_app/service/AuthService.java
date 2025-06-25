package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.dto.RegisterDto;
import com.ridvansevik.library_app.model.User;
import com.ridvansevik.library_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ridvansevik.library_app.model.Role;
/**
 * Service class for handling authentication-related business logic,
 * such as user registration.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user in the system.
     *
     * @param registerDto A DTO containing the new user's username and password.
     * @return The newly created and saved User entity.
     * @throws IllegalStateException if the username is already taken.
     */
    public User register(RegisterDto registerDto) {

        // Check if a user with the given username already exists to prevent duplicates.
        userRepository.findByUsername(registerDto.getUsername()).ifPresent(user -> {
            throw new IllegalStateException(String.format("Username '%s' is already taken.", registerDto.getUsername()));
        });

        // Encode the user's plain-text password for secure storage.
        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());

        // Create a new User entity with the provided details and a default role.
        User newUser = new User(
                registerDto.getUsername(),
                encodedPassword,
                Role.ROLE_USER
        );

        // Save the new user to the database and return the persisted entity.
        return userRepository.save(newUser);
    }
}