package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.exception.ResourceNotFoundException;
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
    /**
     * Retrieves all books from the database.
     *
     * @return A list of all Book entities.
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Retrieves a single book by its ID.
     *
     * @param id The ID of the book to find.
     * @return An Optional containing the found book, or an empty Optional if not found.
     */
    public Optional<Book> getBookById(long id) {
        return bookRepository.findById(id);
    }

    /**
     * Creates and saves a new book.
     *
     * @param book The Book entity to be saved.
     * @return The saved Book entity with its generated ID.
     */
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Updates an existing book's details.
     *
     * @param id          The ID of the book to update.
     * @param bookDetails A Book object containing the new details.
     * @return The updated and saved Book entity.
     * @throws ResourceNotFoundException if no book with the given ID is found.
     */
    public Book updateBook(Long id, Book bookDetails) {
        // Find the existing book or throw a specific exception if it doesn't exist.
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Update the fields of the existing book with the new details.
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());

        // Save the updated book to the database.
        return bookRepository.save(book);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param id The ID of the book to delete.
     * @throws ResourceNotFoundException if no book with the given ID is found.
     */
    public void deleteBook(long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Silme işlemi başarısız: " + id + " ID'li kitap bulunamadı.");
        }

        if (loanRepository.existsByBookId(id)) {
            throw new IllegalStateException("Bu kitap şuan birisi tarafından ödünç alınmış halde, silinemez.");
        }

        bookRepository.deleteById(id);
    }


    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bookRepository.findAll(pageable);
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword, pageable);
    }
}