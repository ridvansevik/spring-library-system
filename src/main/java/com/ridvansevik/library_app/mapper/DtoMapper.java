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
    return new UserDto(user.getId(), user.getUsername(), user.getRole());
}


public BookDto toBookDto(Book book){
    if(book==null) return null;

    return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getIsbn(), book.getBookStatus());
}

public LoanDto toLoanDto(Loan loan){
    if(loan==null) return null;

    return new LoanDto(loan.getId(), toBookDto(loan.getBook()),toUserDto(loan.getUser()),loan.getLoanDate(),loan.getDueDate(),loan.getReturnDate(),loan.getFineAmount());
}

    public Book toBookEntity(CreateUpdateBookDto dto) {
        if (dto == null) return null;

        Book book = new Book();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        return book;
    }

    public CreateUpdateBookDto toCreateUpdateBookDto(Book book) {
        if (book == null) return null;

        return new CreateUpdateBookDto(book.getTitle(),book.getAuthor(), book.getIsbn());
    }

    public void updateBookFromDto(CreateUpdateBookDto dto, Book book) {
        if (dto == null || book == null) {
            return;
        }
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
    }

}
