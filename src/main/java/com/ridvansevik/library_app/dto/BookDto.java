package com.ridvansevik.library_app.dto;

import com.ridvansevik.library_app.model.BookStatus;

public record BookDto(
        Long id,
        String title,
        String author,
        String isbn,
        BookStatus bookStatus
) {}