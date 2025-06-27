package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.service.BookService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

private final BookService bookService;

@GetMapping
public List<Book> getAllBooks(@RequestParam(required = false)String keyword)
{
return bookService.searchBook(keyword);

}

@GetMapping("/{id}")
public ResponseEntity<Book> getBookById(@PathVariable long id){
    return bookService.getBookById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PostMapping
public ResponseEntity<Book> createBook(@Valid @RequestBody Book book){

    Book createdBook = bookService.createBook(book);

    return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
}

@PutMapping("/{id}")
public ResponseEntity<Book> updateBook(@Valid @PathVariable long id, @RequestBody Book book){
    Book updatedBook = bookService.updateBook(id, book);
    return ResponseEntity.ok(updatedBook);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable long id){
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
}

}
