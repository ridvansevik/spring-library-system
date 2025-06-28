package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.dto.RegisterDto;
import com.ridvansevik.library_app.exception.UsernameAlreadyExistsException;
import com.ridvansevik.library_app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST Controller for handling authentication requests like user registration.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth") // All endpoints in this controller will be prefixed with /api/auth
public class AuthController {

    // Injects the AuthService using constructor injection (handled by Lombok's @RequiredArgsConstructor)
    private final AuthService authService;

    /**
     * Handles POST requests to /api/auth/register to create a new user.
     * @param registerDto The user registration data, deserialized from the request body.
     * @return A ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterDto registerDto){
        try{
            // Delegate the core logic to the service layer
            authService.register(registerDto);
            // On success, return 200 OK with a message
            return ResponseEntity.ok("Kullanıcı başarıyla kaydedildi.");
        } catch (UsernameAlreadyExistsException e){
            // If a known error occurs (e.g., user already exists), return 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}