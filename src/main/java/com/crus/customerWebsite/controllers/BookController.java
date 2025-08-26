package com.crus.customerWebsite.controllers;

import com.crus.customerWebsite.models.Book;
import com.crus.customerWebsite.models.Customer;
import com.crus.customerWebsite.services.BookService;
import com.crus.customerWebsite.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    CustomerService customerService;

    @GetMapping("/books")
    public String viewHomePage(Model model) {
        final List<Book> bookList = bookService.getBooks();
        model.addAttribute("bookList", bookList);
        return "books";
    }

    @GetMapping("/new-book")
    public String showNewBookPage(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);
        return "new-book";
    }

    @PostMapping("/books")
    public String saveBook(@ModelAttribute("book") Book book, Model model) {
        try {
            bookService.saveBook(book);
        } catch (IllegalArgumentException e) {
            model.addAttribute("message", "Could not save book, " + e.getMessage());
            return "error";
        }
        return "redirect:/books";
    }

    @RequestMapping("/remove/{id}")
    public String removeBook(@PathVariable(name = "id") Long bookId) {
        Book book = bookService.getBook(bookId);
        book.setCustomer(null);
        bookService.saveBook(book);
        return "redirect:/";
    }

    @GetMapping("/books/assign/{id}")
    public String assignBook(@PathVariable(name = "id") Long id, Model model) {
        Customer customer = customerService.getCustomer(id);
        List<Book> bookList = bookService.getAvailableBooks();
        model.addAttribute("customer", customer);
        model.addAttribute("bookList", bookList);
        return "assign-book";
    }

    @PostMapping("/books/assign")
    public String saveBookAssignment(@RequestParam("customerId") Long customerId, @RequestParam("bookId") Long bookId) {
        Book book = bookService.getBook(bookId);
        book.setCustomer(customerService.getCustomer(customerId));
        bookService.saveBook(book);
        return "redirect:/";
    }
}
