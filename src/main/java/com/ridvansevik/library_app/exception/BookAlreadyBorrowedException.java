package com.ridvansevik.library_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyBorrowedException extends RuntimeException {

    public BookAlreadyBorrowedException(Long bookId) {
        super(String.format("ID'si %d olan kitap zaten ödünç alınmış durumda.", bookId));
    }
}