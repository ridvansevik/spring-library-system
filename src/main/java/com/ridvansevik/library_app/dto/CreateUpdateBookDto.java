package com.ridvansevik.library_app.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUpdateBookDto(
        @NotBlank(message = "Başlık boş olamaz")
        String title,

        @NotBlank(message = "Yazar boş olamaz")
        String author,

        String isbn
) {}