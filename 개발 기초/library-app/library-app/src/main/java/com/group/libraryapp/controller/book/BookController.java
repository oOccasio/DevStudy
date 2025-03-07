package com.group.libraryapp.controller.book;

import com.group.libraryapp.dto.book.request.LoanBookRequest;
import com.group.libraryapp.dto.book.request.ReturnBookRequest;
import com.group.libraryapp.dto.book.request.SaveBookRequest;
import com.group.libraryapp.service.book.BookService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/book")
    public void saveBook(@RequestBody SaveBookRequest request) {
        bookService.saveBook(request);
    }

    @PostMapping("/book/loan")
    public void loanBook(@RequestBody LoanBookRequest request) {
        bookService.loanBook(request);
    }

    @PutMapping("/book/return")
    public void returnBook(@RequestBody ReturnBookRequest request) {
        bookService.returnBook(request);
    }
}
