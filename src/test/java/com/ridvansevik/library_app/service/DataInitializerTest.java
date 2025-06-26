package com.ridvansevik.library_app.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_whenAdminUserDoesNotExist_shouldCreateAndSaveAdmin() throws Exception {
        // 1. HAZIRLIK (Arrange)
        // Mockito'ya talimat: "findByUsername('admin') çağrıldığında, kullanıcıyı
        // bulamadığını belirtmek için boş bir Optional döndür."
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("VerySecretPassword")).thenReturn("encodedAdminPassword");

        // 2. EYLEM (Act)
        // Test edilecek run metodunu çağırıyoruz.
        dataInitializer.run();

        // 3. DOĞRULAMA (Assert)
        // Davranış doğrulaması:
        // - findByUsername metodunun 1 kez çağrıldığını doğrula.
        verify(userRepository, times(1)).findByUsername("admin");
        // - save metodunun 1 kez çağrıldığını doğrula.
        verify(userRepository, times(1)).save(any(User.class));
        // - encode metodunun 1 kez çağrıldığını doğrula.
        verify(passwordEncoder, times(1)).encode("VerySecretPassword");
    }

    @Test
    void run_whenAdminUserExists_shouldDoNothing() throws Exception {
        // 1. HAZIRLIK (Arrange)
        // Mockito'ya talimat: "findByUsername('admin') çağrıldığında, bu kullanıcının
        // zaten var olduğunu belirtmek için dolu bir Optional (içeriği önemli değil) döndür."
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));

        // 2. EYLEM (Act)
        dataInitializer.run();

        // 3. DOĞRULAMA (Assert)
        // Davranış doğrulaması:
        // - findByUsername metodunun 1 kez çağrıldığını doğrula.
        verify(userRepository, times(1)).findByUsername("admin");
        // - Kullanıcı zaten var olduğu için, save metodunun HİÇ çağrılmadığını doğrula.
        verify(userRepository, never()).save(any(User.class));
        // - Aynı şekilde, passwordEncoder'ın da HİÇ çağrılmadığını doğrula.
        verify(passwordEncoder, never()).encode(anyString());
    }
}