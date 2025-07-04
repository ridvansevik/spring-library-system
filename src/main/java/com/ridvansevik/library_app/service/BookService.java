package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.exception.BookIsOnLoanException;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.mapper.DtoMapper;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.ridvansevik.library_app.repository.LoanRepository;
import java.util.List;
import java.util.Optional;

/**
 * Service class that manages business logic for Book entities (CRUD operations).
 */
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final DtoMapper dtoMapper;
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public Optional<Book> getBookById(long id) {
        return bookRepository.findById(id);
    }


    public Book createBook(CreateUpdateBookDto createUpdateBookDto) {
        Book newBook = dtoMapper.toBookEntity(createUpdateBookDto);
        return bookRepository.save(newBook);
    }


    public Book updateBook(Long id, CreateUpdateBookDto createUpdateBookDto) {
        // Find the existing book or throw a specific exception if it doesn't exist.
        Book bookToUpdate = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        dtoMapper.updateBookFromDto(createUpdateBookDto, bookToUpdate);

        // Save the updated book to the database.
        return bookRepository.save(bookToUpdate);
    }

    public void deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silme işlemi başarısız: " + id + " ID'li kitap bulunamadı.");
        }

        loanRepository.findByBookIdAndReturnDateIsNull(id)
                .ifPresent(loan -> {
                    throw new BookIsOnLoanException(id);
                });

        bookRepository.deleteById(id);
    }


    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable);
    }
}