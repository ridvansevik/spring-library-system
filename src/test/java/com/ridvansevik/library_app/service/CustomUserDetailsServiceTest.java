package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.model.Role;
import com.ridvansevik.library_app.model.User;
import com.ridvansevik.library_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        // 1. HAZIRLIK (Arrange)
        String username = "testuser";
        User expectedUser = new User(username, "password123", Role.ROLE_USER);

        // Mockito'ya talimat: "userRepository.findByUsername('testuser') çağrıldığında,
        // 'expectedUser' nesnesini bir Optional içinde sarmalayarak döndür."
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // 2. EYLEM (Act)
        // Test edilecek olan asıl metodu çağırıyoruz.
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // 3. DOĞRULAMA (Assert)
        // Dönen UserDetails nesnesinin boş olmadığını ve kullanıcı adının
        // beklediğimiz gibi olduğunu kontrol ediyoruz.
        assertNotNull(userDetails);
        assertEquals(expectedUser.getUsername(), userDetails.getUsername());
        assertEquals(expectedUser.getPassword(), userDetails.getPassword());

        // Davranış doğrulaması:
        // findByUsername metodunun tam olarak 1 kez çağrıldığını doğruluyoruz.
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void loadUserByUsername_whenUserNotFound_shouldThrowUsernameNotFoundException() {
        // 1. HAZIRLIK (Arrange)
        String nonExistentUsername = "ghostuser";

        // Mockito'ya talimat: "userRepository.findByUsername('ghostuser') çağrıldığında,
        // kullanıcıyı bulamadığını belirtmek için boş bir Optional döndür."
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        // 2. EYLEM & 3. DOĞRULAMA (Act & Assert)
        // Beklenen hatanın (UsernameNotFoundException) fırlatılıp fırlatılmadığını kontrol ediyoruz.
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(nonExistentUsername);
        });

        // Hata mesajının beklediğimiz gibi olduğunu doğruluyoruz.
        assertEquals("Kullanıcı bulunamadı: " + nonExistentUsername, exception.getMessage());

        // Davranış doğrulaması:
        verify(userRepository, times(1)).findByUsername(nonExistentUsername);
    }
}