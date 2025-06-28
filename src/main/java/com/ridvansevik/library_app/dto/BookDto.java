package com.ridvansevik.library_app.dto;

import com.ridvansevik.library_app.model.BookStatus;
import lombok.Data;

@Data
public class BookDto {

    private Long id;
private String title;
private String author;
private String isbn;
private BookStatus bookStatus;
}
