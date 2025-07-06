package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.model.Role;
import com.ridvansevik.library_app.model.User;
import com.ridvansevik.library_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin1").isEmpty()) {
            User admin = new User(
                    "admin1",
                    passwordEncoder.encode("haha"),
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
        }
    }
}