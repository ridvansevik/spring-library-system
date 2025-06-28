package com.ridvansevik.library_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUpdateBookDto {

    @NotBlank(message = "Başlık boş olamaz")
private String title;
    @NotBlank(message = "Yazar boş olamaz")
private String author;
private String isbn;
}
