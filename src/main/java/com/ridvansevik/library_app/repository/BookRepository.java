package com.ridvansevik.library_app.repository;

import com.ridvansevik.library_app.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
