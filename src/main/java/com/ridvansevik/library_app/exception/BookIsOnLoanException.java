package com.ridvansevik.library_app.exception;

public class BookIsOnLoanException extends RuntimeException{
    public BookIsOnLoanException(Long bookId){
        super(String.format("%d Id'li Kitap şuan ödünç alınmış durumda.",bookId));
    }
}
