package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.dto.BookDto;
import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.service.BookService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ridvansevik.library_app.mapper.DtoMapper;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

private final BookService bookService;
private final DtoMapper dtoMapper;
@GetMapping
public Page<BookDto> getAllBooks(@RequestParam(required = false)String keyword, Pageable pageable)
{
  Page<Book> bookPage =  bookService.searchBooks(keyword,pageable);
    return bookPage.map(dtoMapper::toBookDto);
}

@GetMapping("/{id}")
public ResponseEntity<BookDto> getBookById(@PathVariable long id){
    return bookService.getBookById(id)
            .map(dtoMapper::toBookDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@PostMapping
public ResponseEntity<BookDto> createBook(@Valid @RequestBody CreateUpdateBookDto createUpdateBookDto){

    Book createdBook = bookService.createBook(createUpdateBookDto);

    return new ResponseEntity<>(dtoMapper.toBookDto(createdBook), HttpStatus.CREATED);
}

@PutMapping("/{id}")
public ResponseEntity<BookDto> updateBook(@Valid @PathVariable long id, @RequestBody CreateUpdateBookDto createUpdateBookDto){
    Book updatedBook = bookService.updateBook(id, createUpdateBookDto);
    return ResponseEntity.ok(dtoMapper.toBookDto(updatedBook));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteBook(@PathVariable long id){
    bookService.deleteBook(id);
    return ResponseEntity.noContent().build();
}

}
