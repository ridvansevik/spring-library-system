package com.ridvansevik.library_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserHasOverdueBooksOrUnpaidFinesException extends RuntimeException {

    public UserHasOverdueBooksOrUnpaidFinesException(String username) {
        super(String.format("Kullanıcı '%s' gecikmiş bir kitaba veya ödenmemiş bir cezaya sahip olduğu için yeni bir kitap ödünç alamaz.", username));
    }
}