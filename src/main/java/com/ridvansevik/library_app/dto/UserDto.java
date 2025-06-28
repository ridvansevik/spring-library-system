package com.ridvansevik.library_app.dto;

import com.ridvansevik.library_app.model.Role;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    private String username;
    private Role role;
}
