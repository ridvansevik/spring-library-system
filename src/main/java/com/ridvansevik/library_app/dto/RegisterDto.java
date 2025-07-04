package com.ridvansevik.library_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record RegisterDto(@NotBlank String username,@NotBlank String password){}