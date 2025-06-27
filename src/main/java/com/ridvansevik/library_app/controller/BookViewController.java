package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.service.BookService;
import com.ridvansevik.library_app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class BookViewController {

    private final BookService bookService;
    private final LoanService loanService;

    @GetMapping("/books")
    public String getAllBooks(Model model , Principal principal){

        if(principal != null){
            model.addAttribute("username", principal.getName());
        }

        Map<Long,String> borrowersMap = loanService.findActiveLoans().stream()
                        .collect(Collectors.toMap(loan -> loan.getBook().getId(),loan -> loan.getUser().getUsername()));

        model.addAttribute("books",bookService.getAllBooks());
        model.addAttribute("borrowersMap", borrowersMap);
        return "books";
    }

    @GetMapping("/books/new")
    public String showCreateForm(Model model){

        model.addAttribute("book",new Book());
        return "book-form";
    }

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute("book") Book book){
        bookService.createBook(book);
        return "redirect:/web/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id,Model model){
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz Kitap Id"));
        model.addAttribute("book",book);
        return "book-form";
    }

    @PostMapping("books/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute("book") Book book){
        bookService.updateBook(id,book);
        return "redirect:/web/books";
    }

    @PostMapping("books/delete/{id}")
    public String deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return "redirect:/web/books";
    }


    @PostMapping("books/borrow/{id}")
    public String borrowBook(@PathVariable Long id,Principal principal){

        if(principal != null){
            loanService.borrowBook(id,principal.getName());
        }

        return "redirect:/web/books";

    }

    @PostMapping("books/return/{id}")
    public String returnBook(@PathVariable Long id, Principal principal){
        if (principal != null) {
            loanService.returnBook(id, principal.getName());
        }
        return "redirect:/web/books";
    }

}
