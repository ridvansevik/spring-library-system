package com.ridvansevik.library_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE book SET deleted = true WHERE id=?") // Intercepts delete commands
@Where(clause = "deleted=false") // Ensures all queries only find non-deleted books
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Başlık alanı boş olamaz")
    private String title;

    @NotBlank(message = "Yazar alanı boş olamaz")
    private String author;

    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus bookStatus = BookStatus.AVAILABLE;

    @Column(columnDefinition = "boolean default false")
    private boolean deleted = Boolean.FALSE;
}