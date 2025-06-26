package com.ridvansevik.library_app.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void getAuthorities_whenUserHasRoleUser_shouldReturnRoleUserAuthority() {
        // Hazırlık (Arrange)
        User user = new User("testuser", "password", Role.ROLE_USER);

        // Eylem (Act)
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        // Doğrulama (Assert)
        assertNotNull(authorities);
        assertEquals(1, authorities.size()); // Sadece bir yetkisi olmalı
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER"))); // Yetkinin doğru olduğundan emin ol
    }

    @Test
    void getAuthorities_whenUserHasRoleAdmin_shouldReturnRoleAdminAuthority() {
        // Hazırlık (Arrange)
        User adminUser = new User("admin", "password", Role.ROLE_ADMIN);

        // Eylem (Act)
        Collection<? extends GrantedAuthority> authorities = adminUser.getAuthorities();

        // Doğrulama (Assert)
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void accountStatusMethods_shouldReturnTrue() {
        // Hazırlık
        User user = new User();

        // Eylem ve Doğrulama
        assertTrue(user.isAccountNonExpired(), "Account should be non-expired");
        assertTrue(user.isAccountNonLocked(), "Account should be non-locked");
        assertTrue(user.isCredentialsNonExpired(), "Credentials should be non-expired");
        assertTrue(user.isEnabled(), "Account should be enabled");
    }
}