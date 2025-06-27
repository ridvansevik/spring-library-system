package com.ridvansevik.library_app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameAlreadyExistsException  extends RuntimeException{
    public UsernameAlreadyExistsException(String username){
        super(String.format("'%s' kullanıcı adı zaten alınmış.",username));
    }
}
