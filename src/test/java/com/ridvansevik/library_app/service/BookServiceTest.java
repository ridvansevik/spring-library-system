package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.exception.BookIsOnLoanException;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.model.BookStatus;
import com.ridvansevik.library_app.repository.BookRepository;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_shouldSaveAndReturnBook() {
        // Hazırlık (Arrange)
        CreateUpdateBookDto bookDto = new CreateUpdateBookDto();
        bookDto.setTitle("Test Başlık");
        bookDto.setAuthor("Test Yazar");
        bookDto.setIsbn("12345");

        Book bookToSave = new Book(null, "Test Başlık", "Test Yazar", "12345", BookStatus.AVAILABLE);
        Book savedBook = new Book(1L, "Test Başlık", "Test Yazar", "12345", BookStatus.AVAILABLE);

        // ArgumentCaptor ile save metoduna gönderilen Book nesnesini yakala
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        when(bookRepository.save(bookArgumentCaptor.capture())).thenReturn(savedBook);

        // Eylem (Act)
        Book result = bookService.createBook(bookDto);

        // Doğrulama (Assert)
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Başlık", result.getTitle());

        // Yakalanan argümanın doğru değerlere sahip olduğunu kontrol et
        assertEquals("Test Başlık", bookArgumentCaptor.getValue().getTitle());
        assertEquals(BookStatus.AVAILABLE, bookArgumentCaptor.getValue().getBookStatus());
    }

    @Test
    void deleteBook_whenBookExistsAndNotOnLoan_shouldDeleteBook() {
        // Hazırlık
        long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.existsByBookId(bookId)).thenReturn(false);
        doNothing().when(bookRepository).deleteById(bookId);

        // Eylem
        bookService.deleteBook(bookId);

        // Doğrulama
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_whenBookIsOnLoan_shouldThrowBookIsOnLoanException() {
        // Hazırlık
        long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        when(loanRepository.existsByBookId(bookId)).thenReturn(true); // Kitap ödünçte

        // Eylem ve Doğrulama
        assertThrows(BookIsOnLoanException.class, () -> {
            bookService.deleteBook(bookId);
        });

        // Kitap silinmemeli
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteBook_whenBookNotFound_shouldThrowResourceNotFoundException() {
        // Hazırlık
        long bookId = 99L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Eylem ve Doğrulama
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(bookId);
        });

        verify(loanRepository, never()).existsByBookId(anyLong());
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void updateBook_whenBookExists_shouldReturnUpdatedBook() {
        // Hazırlık
        long bookId = 1L;
        CreateUpdateBookDto updatedDetailsDto = new CreateUpdateBookDto();
        updatedDetailsDto.setTitle("Yeni Başlık");
        updatedDetailsDto.setAuthor("Yeni Yazar");
        updatedDetailsDto.setIsbn("54321");

        Book existingBook = new Book(bookId, "Eski Başlık", "Eski Yazar", "12345", BookStatus.AVAILABLE);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        // save metodunun herhangi bir Book nesnesi ile çağrıldığında ne döndüreceğini ayarla
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Eylem
        Book result = bookService.updateBook(bookId, updatedDetailsDto);

        // Doğrulama
        assertNotNull(result);
        assertEquals("Yeni Başlık", result.getTitle());
        assertEquals("Yeni Yazar", result.getAuthor());
        assertEquals(BookStatus.AVAILABLE, result.getBookStatus()); // Durumun korunup korunmadığını kontrol et

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_whenBookNotFound_shouldThrowResourceNotFoundException() {
        // Hazırlık
        long nonExistentBookId = 99L;
        CreateUpdateBookDto updatedDetailsDto = new CreateUpdateBookDto();
        updatedDetailsDto.setTitle("Yeni Başlık");

        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // Eylem & Doğrulama
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(nonExistentBookId, updatedDetailsDto);
        });

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_shouldReturnListOfAllBooks() {
        // 1. HAZIRLIK (Arrange)
        // Repository'den dönmesini beklediğimiz sahte kitap listesini oluşturuyoruz.
        Book book1 = new Book(1L, "Kitap 1", "Yazar 1", "111",BookStatus.AVAILABLE);
        Book book2 = new Book(2L, "Kitap 2", "Yazar 2", "222",BookStatus.AVAILABLE);
        List<Book> expectedBooks = Arrays.asList(book1, book2);

        // Mockito'ya talimat veriyoruz:
        // "bookRepository.findAll() metodu çağrıldığında, yukarıda oluşturduğumuz 'expectedBooks' listesini döndür."
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // 2. EYLEM (Act)
        // Test etmek istediğimiz asıl metodu çağırıyoruz.
        List<Book> actualBooks = bookService.getAllBooks();

        // 3. DOĞRULAMA (Assert)
        // Dönen listenin boş olmadığından ve beklediğimiz boyutta olduğundan emin oluyoruz.
        assertNotNull(actualBooks);
        assertEquals(2, actualBooks.size());
        // Dönen listenin, hazırladığımız liste ile aynı olduğundan emin oluyoruz.
        assertEquals(expectedBooks, actualBooks);

        // Davranış doğrulaması:
        // findAll() metodunun tam olarak 1 kez çağrıldığını kontrol ediyoruz.
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById_whenBookExists_shouldReturnOptionalOfBook() {
        // 1. HAZIRLIK (Arrange)
        long bookId = 1L;
        Book expectedBook = new Book(bookId, "Test Kitabı", "Test Yazarı", "12345",BookStatus.AVAILABLE);

        // Mockito'ya talimat veriyoruz:
        // "bookRepository.findById(1L) metodu çağrıldığında, 'expectedBook' nesnesini
        // bir Optional içinde sarmalayarak döndür."
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));

        // 2. EYLEM (Act)
        Optional<Book> actualBookOptional = bookService.getBookById(bookId);

        // 3. DOĞRULAMA (Assert)
        // Dönen Optional'ın boş olmadığından ve doğru kitabı içerdiğinden emin oluyoruz.
        assertTrue(actualBookOptional.isPresent()); // Optional dolu mu?
        assertEquals(expectedBook.getTitle(), actualBookOptional.get().getTitle()); // İçindeki kitabın başlığı doğru mu?

        // Davranış doğrulaması:
        verify(bookRepository, times(1)).findById(bookId);
    }
}