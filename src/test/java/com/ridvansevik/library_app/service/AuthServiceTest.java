package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.dto.RegisterDto;
import com.ridvansevik.library_app.model.Role;
import com.ridvansevik.library_app.model.User;
import com.ridvansevik.library_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenUsernameIsNew_shouldSaveAndReturnUser() {
        // 1. HAZIRLIK (Arrange)
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("yeniKullanici");
        registerDto.setPassword("Sifre123!");

        // Mockito'ya talimatlar:
        // - "userRepository.findByUsername çağrıldığında, kullanıcıyı bulamadığını belirtmek için boş Optional dön."
        when(userRepository.findByUsername("yeniKullanici")).thenReturn(Optional.empty());
        // - "passwordEncoder.encode çağrıldığında, şifrelenmiş bir versiyon döndür."
        when(passwordEncoder.encode("Sifre123!")).thenReturn("sifrelenmisParola");
        // - "userRepository.save çağrıldığında, kaydedilen kullanıcıyı geri döndür."
        //   any(User.class) kullanarak, metoda gönderilen User nesnesinin ne olduğunun önemli olmadığını belirtiyoruz.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. EYLEM (Act)
        User savedUser = authService.register(registerDto);

        // 3. DOĞRULAMA (Assert)
        assertNotNull(savedUser);
        assertEquals("yeniKullanici", savedUser.getUsername());
        assertEquals("sifrelenmisParola", savedUser.getPassword()); // Şifrenin kodlandığından emin ol
        assertEquals(Role.ROLE_USER, savedUser.getRole()); // Rolün doğru atandığından emin ol

        // Davranış doğrulaması:
        verify(userRepository, times(1)).findByUsername("yeniKullanici");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_whenUsernameIsTaken_shouldThrowIllegalStateException() {
        // 1. HAZIRLIK (Arrange)
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("mevcutKullanici");
        registerDto.setPassword("farketmez");

        // Mockito'ya talimat:
        // "userRepository.findByUsername çağrıldığında, bu kullanıcının zaten var olduğunu belirtmek
        // için dolu bir Optional dön."
        when(userRepository.findByUsername("mevcutKullanici")).thenReturn(Optional.of(new User()));

        // 2. EYLEM & 3. DOĞRULAMA (Act & Assert)
        // Beklenen hatanın fırlatıldığını ve hata mesajının doğru olduğunu kontrol et.
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.register(registerDto);
        });

        assertEquals("Username 'mevcutKullanici' is already taken.", exception.getMessage());

        // Davranış doğrulaması:
        // Hata fırlatıldığı için, passwordEncoder'ın veya save metodunun
        // HİÇ çağrılmamış olması gerekir.
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}