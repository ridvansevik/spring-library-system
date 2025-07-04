package com.ridvansevik.library_app.dto;

import com.ridvansevik.library_app.model.Role;

public record UserDto(
        Long id,
        String username,
        Role role
) {}