package com.ridvansevik.library_app.mapper;

import com.ridvansevik.library_app.dto.BookDto;
import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.dto.LoanDto;
import com.ridvansevik.library_app.dto.UserDto;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.model.Loan;
import com.ridvansevik.library_app.model.User;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

public UserDto toUserDto(User user){
    if(user==null){
        return null;
    }
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setRole(user.getRole());
    userDto.setUsername(user.getUsername());
    return userDto;
}


public BookDto toBookDto(Book book){
    if(book==null) return null;
    BookDto bookDto = new BookDto();
    bookDto.setId(book.getId());
    bookDto.setBookStatus(book.getBookStatus());
    bookDto.setIsbn(book.getIsbn());
    bookDto.setAuthor(book.getAuthor());
    bookDto.setTitle(book.getTitle());
    return bookDto;
}

public LoanDto toLoanDto(Loan loan){
    if(loan==null) return null;
    LoanDto loanDto = new LoanDto();
    loanDto.setBook(toBookDto(loan.getBook()));
    loanDto.setId(loan.getId());
    loanDto.setUser(toUserDto(loan.getUser()));
    loanDto.setLoanDate(loan.getLoanDate());
    loanDto.setDueDate(loan.getDueDate());
    loanDto.setReturnDate(loan.getReturnDate());
    return loanDto;
}

    public Book toBookEntity(CreateUpdateBookDto dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        return book;
    }

    public CreateUpdateBookDto toCreateUpdateBookDto(Book book) {
        if (book == null) return null;
        CreateUpdateBookDto dto = new CreateUpdateBookDto();
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        return dto;
    }
}
