package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.exception.BookIsOnLoanException;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.mapper.DtoMapper;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final DtoMapper dtoMapper;

    public List<Book> getAllBooks() {
        log.info("Fetching all books.");
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(long id) {
        log.info("Searching for book with ID: {}", id);
        return bookRepository.findById(id);
    }

    public Book createBook(CreateUpdateBookDto createUpdateBookDto) {
        log.info("Creating a new book: {}", createUpdateBookDto.title());
        Book newBook = dtoMapper.toBookEntity(createUpdateBookDto);
        Book savedBook = bookRepository.save(newBook);
        log.info("Successfully created new book with ID: {}", savedBook.getId());
        return savedBook;
    }

    public Book updateBook(Long id, CreateUpdateBookDto createUpdateBookDto) {
        log.info("Updating book with ID: {}", id);
        Book bookToUpdate = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Update failed: Book with ID {} not found.", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });

        dtoMapper.updateBookFromDto(createUpdateBookDto, bookToUpdate);
        Book updatedBook = bookRepository.save(bookToUpdate);
        log.info("Successfully updated book with ID: {}", updatedBook.getId());
        return updatedBook;
    }

    public void deleteBook(long id) {
        log.info("Checking to delete book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            log.error("Delete failed: Book with ID {} not found.", id);
            throw new ResourceNotFoundException("Deletion failed: Book with ID " + id + " not found.");
        }

        loanRepository.findByBookIdAndReturnDateIsNull(id)
                .ifPresent(loan -> {
                    log.warn("Attempted to delete book with ID {} while it is on loan.", id);
                    throw new BookIsOnLoanException(id);
                });

        bookRepository.deleteById(id);
        log.info("Successfully deleted book with ID: {}", id);
    }

    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            log.debug("Paginating all books without a search keyword.");
            return bookRepository.findAll(pageable);
        }
        log.debug("Searching for books with keyword: '{}'", keyword);
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable);
    }
}