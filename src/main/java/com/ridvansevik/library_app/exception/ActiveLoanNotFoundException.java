package com.ridvansevik.library_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ActiveLoanNotFoundException extends RuntimeException {

    public ActiveLoanNotFoundException(Long bookId) {
        super(String.format("ID'si %d olan kitap için aktif bir ödünç kaydı bulunamadı.", bookId));
    }
}