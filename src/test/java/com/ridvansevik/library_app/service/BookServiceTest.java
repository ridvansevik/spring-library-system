package com.ridvansevik.library_app.service;

import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.repository.BookRepository;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_shouldSaveAndReturnBook() {
        // Hazırlık (Arrange)
        Book bookToSave = new Book(null, "Test Başlık", "Test Yazar", "12345");
        Book savedBook = new Book(1L, "Test Başlık", "Test Yazar", "12345");
        when(bookRepository.save(bookToSave)).thenReturn(savedBook);

        // Eylem (Act)
        Book result = bookService.createBook(bookToSave);

        // Doğrulama (Assert)
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Başlık", result.getTitle());
        verify(bookRepository, times(1)).save(bookToSave); // save metodunun 1 kez çağrıldığını doğrula
    }

    @Test
    void deleteBook_whenBookExists_shouldDeleteBook() {
        // Hazırlık
        long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(true);
        // doNothing() mocklanan void metotlar için kullanılır.
        doNothing().when(bookRepository).deleteById(bookId);

        // Eylem
        bookService.deleteBook(bookId);

        // Doğrulama
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void deleteBook_whenBookNotFound_shouldThrowResourceNotFoundException() {
        // Hazırlık
        long bookId = 1L;
        when(bookRepository.existsById(bookId)).thenReturn(false);

        // Eylem ve Doğrulama
        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(bookId);
        });

        // deleteById'nin hiç çağrılmadığını doğrula
        verify(bookRepository, never()).deleteById(bookId);
    }

    @Test
    void updateBook_whenBookExists_shouldReturnUpdatedBook() {
        // 1. HAZIRLIK (Arrange)
        long bookId = 1L;
        // Güncelleme için kullanılacak yeni bilgileri içeren nesne
        Book updatedDetails = new Book(null, "Yeni Başlık", "Yeni Yazar", "54321");
        // Veritabanında varmış gibi davranacak mevcut kitap nesnesi
        Book existingBook = new Book(bookId, "Eski Başlık", "Eski Yazar", "12345");

        // Mockito'ya talimat veriyoruz:
        // "bookRepository.findById(1L) metodu çağrıldığında, sanki veritabanında
        // bulmuş gibi 'existingBook' nesnesini bir Optional içinde döndür."
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        // "bookRepository.save() metodu çağrıldığında, güncellenmiş kitabı döndür."
        // any(Book.class) ile herhangi bir Book nesnesi kaydedildiğinde dedik.
        when(bookRepository.save(any(Book.class))).thenReturn(updatedDetails);

        // 2. EYLEM (Act)
        // Test etmek istediğimiz asıl metodu çağırıyoruz.
        Book result = bookService.updateBook(bookId, updatedDetails);

        // 3. DOĞRULAMA (Assert)
        // Dönen sonucun boş olmadığından ve beklediğimiz gibi olduğundan emin oluyoruz.
        assertNotNull(result);
        assertEquals("Yeni Başlık", result.getTitle()); // Başlık güncellenmiş mi?
        assertEquals("Yeni Yazar", result.getAuthor()); // Yazar güncellenmiş mi?

        // Davranış doğrulaması: Metodumuzun doğru işlemleri yaptığına emin olalım.
        verify(bookRepository, times(1)).findById(bookId); // findById metodu tam olarak 1 kez çağrıldı mı?
        verify(bookRepository, times(1)).save(any(Book.class)); // save metodu tam olarak 1 kez çağrıldı mı?
    }

    @Test
    void updateBook_whenBookNotFound_shouldThrowResourceNotFoundException() {
        // 1. HAZIRLIK (Arrange)
        long nonExistentBookId = 99L;
        Book updatedDetails = new Book(null, "Yeni Başlık", "Yeni Yazar", "54321");

        // Mockito'ya talimat veriyoruz:
        // "bookRepository.findById(99L) metodu çağrıldığında, sanki veritabanında
        // bulamamış gibi boş bir Optional döndür."
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // 2. EYLEM & 3. DOĞRULAMA (Act & Assert)
        // assertThrows, belirli bir kod bloğunun beklenen hatayı fırlatıp fırlatmadığını kontrol eder.
        // Eğer fırlatmazsa test başarısız olur.
        assertThrows(ResourceNotFoundException.class, () -> {
            // Bu kodun ResourceNotFoundException fırlatmasını bekliyoruz.
            bookService.updateBook(nonExistentBookId, updatedDetails);
        });

        // Davranış doğrulaması: Kitap bulunamadığı için save metodunun
        // HİÇ çağrılmamış olması gerekir.
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void getAllBooks_shouldReturnListOfAllBooks() {
        // 1. HAZIRLIK (Arrange)
        // Repository'den dönmesini beklediğimiz sahte kitap listesini oluşturuyoruz.
        Book book1 = new Book(1L, "Kitap 1", "Yazar 1", "111");
        Book book2 = new Book(2L, "Kitap 2", "Yazar 2", "222");
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
        Book expectedBook = new Book(bookId, "Test Kitabı", "Test Yazarı", "12345");

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