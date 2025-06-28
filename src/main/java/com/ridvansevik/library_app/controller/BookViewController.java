package com.ridvansevik.library_app.controller;


import com.ridvansevik.library_app.dto.CreateUpdateBookDto;
import com.ridvansevik.library_app.exception.ActiveLoanNotFoundException;
import com.ridvansevik.library_app.exception.BookAlreadyBorrowedException;
import com.ridvansevik.library_app.exception.BookIsOnLoanException;
import com.ridvansevik.library_app.exception.ResourceNotFoundException;
import com.ridvansevik.library_app.mapper.DtoMapper;
import com.ridvansevik.library_app.model.Book;
import com.ridvansevik.library_app.service.BookService;
import com.ridvansevik.library_app.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class BookViewController {

    private final BookService bookService;
    private final LoanService loanService;
    private final DtoMapper dtoMapper;
    @GetMapping("/books")
    public String getAllBooks(@RequestParam(required = false)String keyword, Model model , Principal principal, Pageable pageable){

        if(principal != null){
            model.addAttribute("username", principal.getName());
        }

        Page<Book> books= bookService.searchBooks(keyword,pageable);

        Map<Long,String> borrowersMap = loanService.findActiveLoans().stream()
                        .collect(Collectors.toMap(loan -> loan.getBook().getId(),loan -> loan.getUser().getUsername()));

        String sortParams = pageable.getSort().stream()
                        .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                                .collect(Collectors.joining());

        model.addAttribute("books",books);
        model.addAttribute("borrowersMap", borrowersMap);
        model.addAttribute("keyword",keyword);
        model.addAttribute("sortParams",sortParams);
        return "books";
    }

    @GetMapping("/books/new")
    public String showCreateForm(Model model){

        model.addAttribute("book",new Book());
        return "book-form";
    }
@PostMapping("/books/save")
  public String saveBook(@ModelAttribute("book") Book book){
        CreateUpdateBookDto createUpdateBookDto = dtoMapper.toCreateUpdateBookDto(book);
        bookService.createBook(createUpdateBookDto);
        return "redirect:/web/books";
  }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Long id,Model model){
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz Kitap Id"));
        model.addAttribute("book",book);
        return "book-form";
    }

    @PostMapping("/books/update/{id}")
  public String updateBook(@PathVariable Long id, @ModelAttribute("book") Book book){
        CreateUpdateBookDto createUpdateBookDto = dtoMapper.toCreateUpdateBookDto(book);
        bookService.updateBook(id,createUpdateBookDto);
        return "redirect:/web/books";
  }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            // On success, add a success message to be displayed on the book list page.
            redirectAttributes.addFlashAttribute("successMessage", "Kitap başarıyla silindi.");
        } catch (BookIsOnLoanException e) {
            // On failure (book is on loan), add the specific error message.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // For any other unexpected errors.
            redirectAttributes.addFlashAttribute("errorMessage", "Beklenmedik bir hata oluştu: " + e.getMessage());
        }
        return "redirect:/web/books";
    }



    @PostMapping("/books/borrow/{id}")
    public String borrowBook(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/web/login";
        }
        try {
            loanService.borrowBook(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Kitap başarıyla ödünç alındı.");

        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "İşlem başarısız: Belirtilen kitap veya kullanıcı bulunamadı.");

        } catch (BookAlreadyBorrowedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kitap zaten ödünç alınmış durumda.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Beklenmedik bir hata oluştu. Lütfen tekrar deneyin.");
        }

        return "redirect:/web/books";
    }

    @PostMapping("/books/return/{id}")
    public String returnBook(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/web/login";
        }
        try {
            loanService.returnBook(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Kitap başarıyla iade edildi.");

        } catch (ActiveLoanNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kitap için aktif bir ödünç kaydı bulunamadı.");

        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bu kitabı iade etme yetkiniz bulunmuyor.");

        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "İşlem başarısız: Kullanıcı bulunamadı.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Beklenmedik bir hata oluştu. Lütfen tekrar deneyin.");
        }

        return "redirect:/web/books";
    }

}
